package com.qlsv.dkmh.service;

import com.qlsv.dkmh.dto.request.DangKyRequest;
import com.qlsv.dkmh.dto.response.LopHocPhanDTO;
import com.qlsv.dkmh.dto.response.MonHocDTO;
import com.qlsv.dkmh.dto.response.PhieuDangKyDTO;
import com.qlsv.dkmh.entity.LopHocPhan;
import com.qlsv.dkmh.entity.MonHoc;
import com.qlsv.dkmh.entity.PhieuDangKy;
import com.qlsv.dkmh.entity.SinhVien;
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

    private LopHocPhanDTO convertToDTO(LopHocPhan lhp) {
        return new LopHocPhanDTO(
                lhp.getMaLop(),
                lhp.getTenLop(),
                lhp.getSoSvToiDa(),
                lhp.getSoSvHienTai(),
                lhp.getKhungGioHoc(),
                lhp.getPhongHoc(),
                lhp.getGiangVien()
        );
    }

    public List<LopHocPhanDTO> getLopHocPhanByMonHoc(String maMH, String hocKy) {
        return lopHocPhanRepo.findByMonHocAndHocKy(maMH, hocKy)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private MonHocDTO convertToDTO(MonHoc mh) {
        return new MonHocDTO(mh.getMaMH(), mh.getTenMH(), mh.getSoTinChi());
    }

    public List<MonHocDTO> getAllMonHocDTO() {
        return monHocRepo.findAllBy().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<MonHocDTO> getMonHocByHocKyDTO(String hocKy) {
        return monHocRepo.findMonHocByHocKy(hocKy).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }



    public PhieuDangKyDTO getPhieuDangKy(String maSV, String hocKy) {
        SinhVien sv = sinhVienRepo.findById(maSV)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sinh viên " + maSV));

        List<PhieuDangKy> dsDangKy = phieuDangKyRepo.findBySinhVien_MaSVAndHocKy(maSV, hocKy);

        return buildRegistrationSlip(sv, hocKy, dsDangKy);
    }


    // --- Hàm Helper: Xây dựng DTO trả về ---
    private PhieuDangKyDTO buildRegistrationSlip(SinhVien sv, String hocKy, List<PhieuDangKy> dsDangKy) {
        PhieuDangKyDTO slip = new PhieuDangKyDTO();
        slip.setMaSV(sv.getMaSV());
        slip.setTenSV(sv.getTen());
        slip.setKhoaHoc(sv.getKhoa());
        slip.setHocKy(hocKy);

        List<PhieuDangKyDTO.RegisteredCourseDTO> cacMonHocDTO = dsDangKy.stream().map(pdk -> {
            LopHocPhan lhp = pdk.getLopHocPhan();
            MonHoc mh = lhp.getMonHoc();

            return new PhieuDangKyDTO.RegisteredCourseDTO(
                    mh.getMaMH(),
                    mh.getTenMH(),
                    mh.getSoTinChi(),
                    lhp.getKhungGioHoc(),
                    lhp.getGiangVien(),
                    lhp.getTenLop(),
                    lhp.getPhongHoc()
            );
        }).collect(Collectors.toList());

        // Tính tổng tín chỉ
        slip.setTongSoTinChi(cacMonHocDTO.stream().mapToInt(PhieuDangKyDTO.RegisteredCourseDTO::getSoTinChi).sum());
        slip.setDanhSachDaDangKy(cacMonHocDTO);
        return slip;
    }

//Tạo phiếu đăng ký mới cho sinh viên
    @Transactional
    public PhieuDangKyDTO createPhieuDangKy(DangKyRequest request) {

        SinhVien sv = sinhVienRepo.findById(request.getMaSV())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sinh viên: " + request.getMaSV()));

        //Kiểm tra số lượng môn đã đăng ký
        int soMonDaDangKy = phieuDangKyRepo.countRegistrationsByMaSVAndHocKy(request.getMaSV(), request.getHocKy());
        if (soMonDaDangKy + request.getDanhSachMaLop().size() > 10) {
            throw new RuntimeException("Vượt quá số lượng môn học tối đa (10 môn)");
        }

        //Xóa tất cả đăng ký cũ (nếu có)
        if (soMonDaDangKy > 0) {
            List<String> danhSachMaLopCu = phieuDangKyRepo.findMaLopByMaSVAndHocKy(request.getMaSV(), request.getHocKy());
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
            PhieuDangKy phieu = new PhieuDangKy();
            phieu.setSinhVien(sv);
            phieu.setLopHocPhan(lhp);
            phieu.setHocKy(request.getHocKy());
            phieuDangKyRepo.save(phieu);

            // Tăng số sinh viên hiện tại
            lopHocPhanRepo.incrementSoSvHienTai(maLop);
        }
        return getPhieuDangKy(request.getMaSV(), request.getHocKy());
    }


//Sửa phiếu đăng ký - Cập nhật danh sách lớp học phần
    @Transactional
    public PhieuDangKyDTO updatePhieuDangKy(DangKyRequest request) {
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

        //Kiểm tra số lượng môn không vượt quá 10
        int soMonSauUpdate = danhSachMaLopCu.size() - danhSachCanXoa.size() + danhSachCanThem.size();
        if (soMonSauUpdate > 10) {
            throw new RuntimeException("Vượt quá số lượng môn học tối đa (10 môn)");
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
            PhieuDangKy phieu = new PhieuDangKy();
            phieu.setSinhVien(sv);
            phieu.setLopHocPhan(lhp);
            phieu.setHocKy(request.getHocKy());
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