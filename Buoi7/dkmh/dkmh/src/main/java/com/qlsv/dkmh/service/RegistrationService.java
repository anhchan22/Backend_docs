package com.qlsv.dkmh.service;

import com.qlsv.dkmh.dto.request.DangKyRequest;
import com.qlsv.dkmh.dto.response.PhieuDangKyResponse;
import com.qlsv.dkmh.entity.LopHocPhan;
import com.qlsv.dkmh.entity.PhieuDangKy;
import com.qlsv.dkmh.entity.SinhVien;
import com.qlsv.dkmh.enums.ErrorCode;
import com.qlsv.dkmh.exception.AppException;
import com.qlsv.dkmh.mapper.PhieuDangKyMapper;
import com.qlsv.dkmh.repository.LopHocPhanRepository;
import com.qlsv.dkmh.repository.MonHocRepository;
import com.qlsv.dkmh.repository.PhieuDangKyRepository;
import com.qlsv.dkmh.repository.SinhVienRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RegistrationService {

    @Autowired private SinhVienRepository sinhVienRepo;
    @Autowired private MonHocRepository monHocRepo;
    @Autowired private LopHocPhanRepository lopHocPhanRepo;
    @Autowired private PhieuDangKyRepository phieuDangKyRepo;
    @Autowired
    private PhieuDangKyMapper phieuDangKyMapper;


    public PhieuDangKyResponse getPhieuDangKy(String maSV, String hocKy) {
        SinhVien sv = sinhVienRepo.findById(maSV)
                .orElseThrow(() -> new AppException(ErrorCode.SV_NOT_FOUND));

        List<PhieuDangKy> dsDangKy = phieuDangKyRepo.findBySinhVien_MaSVAndHocKy(maSV, hocKy);

        // Sử dụng mapper để chuyển đổi
        PhieuDangKyResponse response = new PhieuDangKyResponse();
        response.setMaSV(sv.getMaSV());
        response.setTenSV(sv.getTen());
        response.setKhoaHoc(sv.getKhoa());
        response.setHocKy(hocKy);

        List<PhieuDangKyResponse.MonHocDaDangKyResponse> courses = phieuDangKyMapper.toMonHocDaDangKyResponseList(dsDangKy);
        response.setDanhSachMHDaDangKy(courses);
        response.setTongSoTinChi(courses.stream().mapToInt(PhieuDangKyResponse.MonHocDaDangKyResponse::getSoTinChi).sum());

        return response;
    }


//Tạo phiếu đăng ký mới cho sinh viên
    @Transactional
    public PhieuDangKyResponse createPhieuDangKy(DangKyRequest request) {

        SinhVien sv = sinhVienRepo.findById(request.getMaSV())
                .orElseThrow(() -> new AppException(ErrorCode.SV_NOT_FOUND));

        //Kiểm tra số lượng tín chỉ đã đăng kýuu
        List<PhieuDangKy> dsPhieuDangKyCu = phieuDangKyRepo.findBySinhVien_MaSVAndHocKy(request.getMaSV(), request.getHocKy());

        // Tính số tín chỉ mới sẽ đăng ký
        int soTinChiMoi = 0;
        for (String maLop : request.getDanhSachMaLop()) {
            LopHocPhan lhp = lopHocPhanRepo.findById(maLop)
                    .orElseThrow(() -> new AppException(ErrorCode.LOP_NOT_FOUND));
            soTinChiMoi += lhp.getMonHoc().getSoTinChi();
        }

        // Kiểm tra tổng số tín chỉ sau khi đăng ký mới (do sẽ xóa hết cũ rồi đăng ký lại)
        if (soTinChiMoi < 10) {
            throw new RuntimeException("Số tín chỉ đăng ký phải tối thiểu 10 tín chỉ (hiện tại: " + soTinChiMoi + " tín chỉ)");
        }
        if (soTinChiMoi > 15) {
            throw new RuntimeException("Số tín chỉ đăng ký không được vượt quá 15 tín chỉ (hiện tại: " + soTinChiMoi + " tín chỉ)");
        }

        //Xóa tất cả đăng ký cũ (nếu có)
        if (!dsPhieuDangKyCu.isEmpty()) {
            List<String> danhSachMaLopCu = dsPhieuDangKyCu.stream()
                    .map(pdk -> pdk.getLopHocPhan().getMaLop())
                    .toList();
            // Giảm số sinh viên hiện tại của các lớp cũ
            for (String maLop : danhSachMaLopCu) {
                lopHocPhanRepo.decrementSoSvHienTai(maLop);
            }
            phieuDangKyRepo.deleteBySinhVienAndHocKy(sv, request.getHocKy());
        }

        //Tạo phiếu đăng ký mới
        for (String maLop : request.getDanhSachMaLop()) {
            LopHocPhan lhp = lopHocPhanRepo.findById(maLop)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy lớp học phần: " + maLop));

            // Kiểm tra lớp còn chỗ trống
            if (lhp.getSoSvHienTai() >= lhp.getSoSvToiDa()) {
                throw new RuntimeException("Lớp " + maLop + " đã đầy");
            }

            // Kiểm tra sinh viên đã đăng ký lớp này chưa
            if (phieuDangKyRepo.existsBySinhVienAndLopHocPhanAndHocKy(sv, lhp, request.getHocKy())) {
                throw new RuntimeException("Sinh viên đã đăng ký lớp " + maLop);
            }

            // Tạo phiếu đăng ký
            // LƯU Ý: Không dùng mapper.toEntity(request) vì:
            // 1. Request chỉ có String (maSV, danhSachMaLop) không phải Object
            // 2. Cần SET THỦ CÔNG sinhVien và lopHocPhan sau khi FETCH từ DB
            // 3. Request có List<String> nhưng mỗi PhieuDangKy chỉ có 1 lopHocPhan
            PhieuDangKy phieu = new PhieuDangKy();
            phieu.setHocKy(request.getHocKy());
            phieu.setSinhVien(sv);
            phieu.setLopHocPhan(lhp);
            phieuDangKyRepo.save(phieu);

            // Tăng số sinh viên hiện tại
            lopHocPhanRepo.incrementSoSvHienTai(maLop);
        }
        return getPhieuDangKy(request.getMaSV(), request.getHocKy());
    }


//Sửa phiếu đăng ký - Cập nhật danh sách lớp học phần
    @Transactional
    public PhieuDangKyResponse updatePhieuDangKy(DangKyRequest request) {
        SinhVien sv = sinhVienRepo.findById(request.getMaSV())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sinh viên: " + request.getMaSV()));

        //Lấy danh sách lớp cũ
        List<String> danhSachMaLopCu = phieuDangKyRepo.findMaLopByMaSVAndHocKy(request.getMaSV(), request.getHocKy());

        if (danhSachMaLopCu.isEmpty()) {
            throw new RuntimeException("Không tìm thấy phiếu đăng ký cũ");
        }

        //Tìm lớp cần XÓA (có trong danh sách cũ nhưng không có trong danh sách mới)
        List<String> danhSachCanXoa = danhSachMaLopCu.stream()
                .filter(maLop -> !request.getDanhSachMaLop().contains(maLop))
                .collect(Collectors.toList());

        //Tìm lớp cần THÊM (có trong danh sách mới nhưng không có trong danh sách cũ)
        List<String> danhSachCanThem = request.getDanhSachMaLop().stream()
                .filter(maLop -> !danhSachMaLopCu.contains(maLop))
                .collect(Collectors.toList());

        // Tính số tín chỉ hiện tại (sau khi xóa)
        List<String> danhSachConLai = danhSachMaLopCu.stream()
                .filter(maLop -> !danhSachCanXoa.contains(maLop))
                .toList();

        int soTinChiHienTai = 0;
        for (String maLop : danhSachConLai) {
            LopHocPhan lhp = lopHocPhanRepo.findById(maLop).orElse(null);
            if (lhp != null) {
                soTinChiHienTai += lhp.getMonHoc().getSoTinChi();
            }
        }

        // Tính số tín chỉ sẽ thêm
        int soTinChiThem = 0;
        for (String maLop : danhSachCanThem) {
            LopHocPhan lhp = lopHocPhanRepo.findById(maLop)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy lớp học phần: " + maLop));
            soTinChiThem += lhp.getMonHoc().getSoTinChi();
        }

        int tongSoTinChiSauUpdate = soTinChiHienTai + soTinChiThem;

        // Kiểm tra tổng số tín chỉ sau khi update
        if (tongSoTinChiSauUpdate < 10) {
            throw new RuntimeException("Số tín chỉ đăng ký phải tối thiểu 10 tín chỉ (sau cập nhật: " + tongSoTinChiSauUpdate + " tín chỉ)");
        }
        if (tongSoTinChiSauUpdate > 15) {
            throw new RuntimeException("Số tín chỉ đăng ký không được vượt quá 15 tín chỉ (sau cập nhật: " + tongSoTinChiSauUpdate + " tín chỉ)");
        }

        //XÓA các lớp không còn trong danh sách mới
        for (String maLop : danhSachCanXoa) {
            phieuDangKyRepo.deleteByMaSVAndMaLopAndHocKy(request.getMaSV(), maLop, request.getHocKy());
            // Giảm số sinh viên hiện tại
            lopHocPhanRepo.decrementSoSvHienTai(maLop);
        }

        // THÊM các lớp mới
        for (String maLop : danhSachCanThem) {
            LopHocPhan lhp = lopHocPhanRepo.findById(maLop)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy lớp học phần: " + maLop));

            if (lhp.getSoSvHienTai() >= lhp.getSoSvToiDa()) {
                throw new RuntimeException("Lớp " + maLop + " đã đầy");
            }

            // Tạo phiếu đăng ký mới
            // Tương tự createPhieuDangKy: phải set thủ công vì cần fetch entity từ DB
            PhieuDangKy phieu = new PhieuDangKy();
            phieu.setHocKy(request.getHocKy());
            phieu.setSinhVien(sv);
            phieu.setLopHocPhan(lhp);
            phieuDangKyRepo.save(phieu);

            // Tăng số sinh viên hiện tại
            lopHocPhanRepo.incrementSoSvHienTai(maLop);
        }
        return getPhieuDangKy(request.getMaSV(), request.getHocKy());
    }


//Xóa toàn bộ phiếu đăng ký của sinh viên trong học kỳ
    @Transactional
    public String deletePhieuDangKy(String maSV, String hocKy) {
        SinhVien sv = sinhVienRepo.findById(maSV)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sinh viên: " + maSV));

        //Lấy danh sách lớp đã đăng ký
        List<String> danhSachMaLop = phieuDangKyRepo.findMaLopByMaSVAndHocKy(maSV, hocKy);

        if (danhSachMaLop.isEmpty()) {
            throw new RuntimeException("Không tìm thấy phiếu đăng ký để xóa");
        }
        //Giảm số sinh viên hiện tại của các lớp
        for (String maLop : danhSachMaLop) {
            lopHocPhanRepo.decrementSoSvHienTai(maLop);
        }
        //Xóa tất cả phiếu đăng ký
        phieuDangKyRepo.deleteBySinhVienAndHocKy(sv, hocKy);
        return "Đã xóa thành công " + danhSachMaLop.size() + " môn học trong phiếu đăng ký của sinh viên " + maSV + " học kỳ " + hocKy;
    }


//Xóa một môn học cụ thể trong phiếu đăng ký
    @Transactional
    public String deleteMonHocFromPhieuDangKy(String maSV, String maLop, String hocKy) {
        phieuDangKyRepo.findByMaSVAndMaLopAndHocKy(maSV, maLop, hocKy)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phiếu đăng ký cho lớp " + maLop));
        phieuDangKyRepo.deleteByMaSVAndMaLopAndHocKy(maSV, maLop, hocKy);
        // giam so sinh vien hien tai
        lopHocPhanRepo.decrementSoSvHienTai(maLop);

        return "Đã xóa thành công môn học " + maLop + " khỏi phiếu đăng ký của sinh viên " + maSV + " học kỳ " + hocKy;
    }
}