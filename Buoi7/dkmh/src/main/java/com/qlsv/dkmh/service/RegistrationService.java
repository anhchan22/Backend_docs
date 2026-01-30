package com.qlsv.dkmh.service;

import com.qlsv.dkmh.dto.request.NopPhieuDangKyRequest;
import com.qlsv.dkmh.dto.response.DraftResponse;
import com.qlsv.dkmh.dto.response.PhieuDangKyResponse;
import com.qlsv.dkmh.entity.LopHocPhan;
import com.qlsv.dkmh.entity.PhieuDangKy;
import com.qlsv.dkmh.entity.SinhVien;
import com.qlsv.dkmh.enums.ErrorCode;
import com.qlsv.dkmh.exception.AppException;
import com.qlsv.dkmh.mapper.DraftMapper;
import com.qlsv.dkmh.mapper.PhieuDangKyMapper;
import com.qlsv.dkmh.repository.LopHocPhanRepository;
import com.qlsv.dkmh.repository.PhieuDangKyRepository;
import com.qlsv.dkmh.repository.SinhVienRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RegistrationService {

    @Autowired private SinhVienRepository sinhVienRepo;
    @Autowired private LopHocPhanRepository lopHocPhanRepo;
    @Autowired private PhieuDangKyRepository phieuDangKyRepo;
    @Autowired private PhieuDangKyMapper phieuDangKyMapper;
    @Autowired private DraftMapper draftMapper;


    public PhieuDangKyResponse getPhieuDangKy(String maSV, String hocKy) {
        SinhVien sv = sinhVienRepo.findById(maSV)
                .orElseThrow(() -> new AppException(ErrorCode.SV_NOT_FOUND));

        List<PhieuDangKy> dsDangKy = phieuDangKyRepo.findBySinhVien_MaSVAndHocKy(maSV, hocKy);

        PhieuDangKyResponse response = phieuDangKyMapper.toPhieuDangKyResponseBase(sv);
        response.setHocKy(hocKy);

        List<PhieuDangKyResponse.MonHocDaDangKyResponse> courses = phieuDangKyMapper.toMonHocDaDangKyResponseList(dsDangKy);
        response.setDanhSachMHDaDangKy(courses);
        response.setTongSoTinChi(courses.stream().mapToInt(PhieuDangKyResponse.MonHocDaDangKyResponse::getSoTinChi).sum());
        return response;
    }


    @Transactional
    public String deletePhieuDangKy(String maSV, String hocKy) {
        SinhVien sv = sinhVienRepo.findById(maSV)
                .orElseThrow(() -> new AppException(ErrorCode.SV_NOT_FOUND));

        List<String> danhSachMaLop = phieuDangKyRepo.findMaLopByMaSVAndHocKy(maSV, hocKy);

        if (danhSachMaLop.isEmpty()) {
            throw new AppException(ErrorCode.PHIEU_DANGKY_NOT_FOUND);
        }
        for (String maLop : danhSachMaLop) {
            lopHocPhanRepo.decrementSoSvHienTai(maLop);
        }
        phieuDangKyRepo.deleteBySinhVienAndHocKy(sv, hocKy);
        return "Đã xóa thành công " + danhSachMaLop.size() + " môn học trong phiếu đăng ký của sinh viên " + maSV + " học kỳ " + hocKy;
    }


    private final Map<String, List<String>> draftStorage = new HashMap<>();

    private String getDraftKey(String maSV, String hocKy) {
        return maSV + "::" + hocKy;
    }

    public DraftResponse getDraft(String maSV, String hocKy) {
        String key = getDraftKey(maSV, hocKy);
        List<String> danhSachMaLop = draftStorage.getOrDefault(key, new ArrayList<>());

        DraftResponse response = new DraftResponse();
        response.setMaSV(maSV);
        response.setHocKy(hocKy);

        // Lấy danh sách lớp học phần từ database
        List<LopHocPhan> danhSachLopHocPhan = new ArrayList<>();
        for (String maLop : danhSachMaLop) {
            lopHocPhanRepo.findById(maLop)
                    .filter(lhp -> lhp.getMonHoc() != null)
                    .ifPresent(danhSachLopHocPhan::add);
        }

        // Dùng mapper để convert sang DTO
        List<DraftResponse.LopHocPhanTrongDraft> danhSachLop = draftMapper.toLopHocPhanTrongDraftList(danhSachLopHocPhan);
        response.setDanhSachLopDaChon(danhSachLop);

        int tongTinChi = danhSachLop.stream()
                .mapToInt(DraftResponse.LopHocPhanTrongDraft::getSoTinChi)
                .sum();
        response.setTongSoTinChi(tongTinChi);
        response.setDuDieuKienNop(tongTinChi >= 10 && tongTinChi <= 15);

        return response;
    }

    @Transactional
    public DraftResponse addToDraft(String maSV, String hocKy, String maLop) {
        LopHocPhan lopMoi = lopHocPhanRepo.findById(maLop)
                .orElseThrow(() -> new AppException(ErrorCode.LOP_NOT_FOUND));

        if (lopMoi.getSoSvHienTai() >= lopMoi.getSoSvToiDa()) {
            throw new AppException(ErrorCode.INVALID_SOSV_TOIDA);
        }

        String key = getDraftKey(maSV, hocKy);
        List<String> danhSachMaLop = draftStorage.getOrDefault(key, new ArrayList<>());

        if (danhSachMaLop.contains(maLop)) {
            throw new AppException(ErrorCode.LOP_EXISTED_IN_DRAFT);
        }

        for (String maLopDaChon : danhSachMaLop) {
            LopHocPhan lopDaChon = lopHocPhanRepo.findById(maLopDaChon).orElse(null);
            if (lopDaChon != null && isScheduleConflict(lopDaChon, lopMoi)) {
                throw new AppException(ErrorCode.SCHEDULE_CONFLICT);
            }
        }

        lopHocPhanRepo.incrementSoSvHienTai(maLop);

        danhSachMaLop.add(maLop);
        draftStorage.put(key, danhSachMaLop);

        return getDraft(maSV, hocKy);
    }

    @Transactional
    public DraftResponse removeFromDraft(String maSV, String hocKy, String maLop) {
        String key = getDraftKey(maSV, hocKy);
        List<String> danhSachMaLop = draftStorage.getOrDefault(key, new ArrayList<>());

        if (!danhSachMaLop.remove(maLop)) {
            throw new AppException(ErrorCode.LOP_NOT_FOUND_IN_DRAFT);
        }

        lopHocPhanRepo.decrementSoSvHienTai(maLop);

        if (danhSachMaLop.isEmpty()) {
            draftStorage.remove(key);
        } else {
            draftStorage.put(key, danhSachMaLop);
        }

        return getDraft(maSV, hocKy);
    }

    @Transactional
    public PhieuDangKyResponse submitDraft(NopPhieuDangKyRequest request) {
        String maSV = request.getMaSV();
        String hocKy = request.getHocKy();

        SinhVien sv = sinhVienRepo.findById(maSV)
                .orElseThrow(() -> new AppException(ErrorCode.SV_NOT_FOUND));

        String key = getDraftKey(maSV, hocKy);
        List<String> danhSachMaLop = draftStorage.get(key);

        if (danhSachMaLop == null || danhSachMaLop.isEmpty()) {
            throw new AppException(ErrorCode.DRAFT_EMPTY);
        }

        int tongSoTinChi = 0;
        for (String maLop : danhSachMaLop) {
            LopHocPhan lhp = lopHocPhanRepo.findById(maLop)
                    .orElseThrow(() -> new AppException(ErrorCode.LOP_NOT_FOUND));
            tongSoTinChi += lhp.getMonHoc().getSoTinChi();
        }

        if (tongSoTinChi < 10) {
            throw new AppException(ErrorCode.INVALID_MIN_CREDITS);
        }
        if (tongSoTinChi > 15) {
            throw new AppException(ErrorCode.INVALID_MAX_CREDITS);
        }

        List<PhieuDangKy> dsPhieuDangKyCu = phieuDangKyRepo.findBySinhVien_MaSVAndHocKy(maSV, hocKy);
        if (!dsPhieuDangKyCu.isEmpty()) {
            phieuDangKyRepo.deleteBySinhVienAndHocKy(sv, hocKy);
        }

        for (String maLop : danhSachMaLop) {
            LopHocPhan lhp = lopHocPhanRepo.findById(maLop)
                    .orElseThrow(() -> new AppException(ErrorCode.LOP_NOT_FOUND));

            PhieuDangKy phieu = new PhieuDangKy();
            phieu.setHocKy(hocKy);
            phieu.setSinhVien(sv);
            phieu.setLopHocPhan(lhp);
            phieuDangKyRepo.save(phieu);
        }

        draftStorage.remove(key);

        return getPhieuDangKy(maSV, hocKy);
    }

    private boolean isScheduleConflict(LopHocPhan lop1, LopHocPhan lop2) {
        String khung1 = lop1.getKhungGioHoc();
        String khung2 = lop2.getKhungGioHoc();

        if (khung1 == null || khung2 == null || khung1.trim().isEmpty() || khung2.trim().isEmpty()) {
            return false;
        }
        ScheduleInfo info1 = parseSchedule(khung1);
        ScheduleInfo info2 = parseSchedule(khung2);
        if (info1.thu == 0 || info2.thu == 0 || info1.thu != info2.thu) {
            return false;
        }
        // Quy tắc: khoảng [start, end) – nếu chỉ chạm biên (end == other.start) thì KHÔNG xung đột
        // Xung đột khi hai khoảng giao nhau thực sự: start1 < end2 && start2 < end1
        return (info1.tietBatDau < info2.tietKetThuc) && (info2.tietBatDau < info1.tietKetThuc);
    }

    private ScheduleInfo parseSchedule(String khungGioHoc) {

        ScheduleInfo info = new ScheduleInfo();
        if (khungGioHoc == null) {
            return info;
        }
        String normalized = khungGioHoc.trim().toUpperCase();
        try {
            if (!normalized.startsWith("T")) {
                return info;
            }
            int idxParen = normalized.indexOf('(');
            int idxClose = normalized.indexOf(')');
            if (idxParen < 0 || idxClose < 0 || idxClose <= idxParen) {
                return info;
            }
            String dayPart = normalized.substring(1, idxParen).trim();
            int thu = Integer.parseInt(dayPart); // nếu lỗi sẽ vào catch
            String range = normalized.substring(idxParen + 1, idxClose).trim();
            String[] parts = range.split("-");
            if (parts.length != 2) {
                return info;
            }
            int start = Integer.parseInt(parts[0].trim());
            int end = Integer.parseInt(parts[1].trim());
            if (thu < 2 || thu > 7 || start <= 0 || end <= 0 || end <= start) {
                return info; // giữ thu = 0 => invalid
            }
            info.thu = thu;
            info.tietBatDau = start;
            info.tietKetThuc = end;
        } catch (Exception e) {
        }
        return info;
    }

    private static class ScheduleInfo {
        int thu = 0;
        int tietBatDau = 0;
        int tietKetThuc = 0;
    }
}
