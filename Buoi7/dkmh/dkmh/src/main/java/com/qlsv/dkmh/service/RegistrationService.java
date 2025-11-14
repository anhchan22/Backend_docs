package com.qlsv.dkmh.service;

import com.qlsv.dkmh.dto.RegistrationRequest;
import com.qlsv.dkmh.dto.RegistrationSlipDTO;
import com.qlsv.dkmh.entity.LopHocPhan;
import com.qlsv.dkmh.entity.MonHoc;
import com.qlsv.dkmh.entity.PhieuDangKy;
import com.qlsv.dkmh.entity.SinhVien;
import com.qlsv.dkmh.repository.LopHocPhanRepository;
import com.qlsv.dkmh.repository.MonHocRepository;
import com.qlsv.dkmh.repository.PhieuDangKyRepository;
import com.qlsv.dkmh.repository.SinhVienRepository;
import jakarta.transaction.Transactional; // Quan trọng
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RegistrationService {

    @Autowired private SinhVienRepository sinhVienRepo;
    @Autowired private MonHocRepository monHocRepo;
    @Autowired private LopHocPhanRepository lopHocPhanRepo;
    @Autowired private PhieuDangKyRepository phieuDangKyRepo;

    public List<MonHoc> getAllMonHoc() {
        return monHocRepo.findAll();
    }

    // --- API 1: Lấy Môn Học ---
    public List<MonHoc> getMonHocByHocKy(String hocKy) {
        return monHocRepo.findMonHocByHocKy(hocKy);
    }

    // --- API 2: Lấy Lớp Học Phần ---
    public List<LopHocPhan> getLopHocPhanByMonHoc(String maMH, String hocKy) {
        return lopHocPhanRepo.findByMonHoc_MaMHAndHocKy(maMH, hocKy);
    }

    // --- API 3: Lấy Phiếu Đăng Ký (Helper) ---
    public RegistrationSlipDTO getPhieuDangKy(String maSV, String hocKy) {
        SinhVien sv = sinhVienRepo.findById(maSV)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sinh viên " + maSV));

        List<PhieuDangKy> dsDangKy = phieuDangKyRepo.findBySinhVien_MaSVAndHocKy(maSV, hocKy);

        return buildRegistrationSlip(sv, hocKy, dsDangKy); // Gọi hàm helper bên dưới
    }

    // --- API 4: XỬ LÝ ĐĂNG KÝ (Logic chính) ---
    @Transactional // Nếu có lỗi, tất cả thay đổi CSDL sẽ bị HỦY BỎ
    public RegistrationSlipDTO registerCourses(RegistrationRequest request) {

        // B1: Lấy dữ liệu cần thiết
        SinhVien sv = sinhVienRepo.findById(request.getMaSV())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sinh viên " + request.getMaSV()));

        List<LopHocPhan> dsLopMoi = lopHocPhanRepo.findAllById(request.getDanhSachMaLop());

        // Đã xóa logic kiểm tra môn tiên quyết

        // B2: Khởi tạo biến kiểm tra ràng buộc
        int tongSoTinChi = 0;
        Set<String> monDaChonTrongRequest = new HashSet<>();
        Set<String> gioDaChonTrongRequest = new HashSet<>();

        // B3: Vòng lặp kiểm tra các ràng buộc
        for (LopHocPhan lop : dsLopMoi) {
            MonHoc mon = lop.getMonHoc();

            // Ràng buộc 6: Với mỗi môn học, một sinh viên chỉ được đăng kí vào 1 lớp
            if (monDaChonTrongRequest.contains(mon.getMaMH())) {
                throw new RuntimeException("Lỗi: Không được đăng ký 2 lớp cho môn " + mon.getTenMH());
            }
            monDaChonTrongRequest.add(mon.getMaMH());

            // Ràng buộc 5: Sinh viên không được phép đăng kí học hai lớp có trùng buổi học
            if (gioDaChonTrongRequest.contains(lop.getKhungGioHoc())) {
                throw new RuntimeException("Lỗi: Trùng lịch học vào " + lop.getKhungGioHoc());
            }
            gioDaChonTrongRequest.add(lop.getKhungGioHoc());

            // Đã xóa logic kiểm tra môn tiên quyết

            // Ràng buộc (phụ): Kiểm tra lớp đầy
            if (lop.getSoSvHienTai() >= lop.getSoSvToiDa()) {
                throw new RuntimeException("Lỗi: Lớp " + lop.getTenLop() + " đã đầy");
            }

            // Tính tổng tín chỉ
            tongSoTinChi += mon.getSoTinChi();
        }

        // B4: Kiểm tra ràng buộc Tín chỉ Min/Max
        if (tongSoTinChi < 10) {
            throw new RuntimeException("Lỗi: Chưa đăng ký đủ 10 tín chỉ (hiện tại: " + tongSoTinChi + " tín chỉ)");
        }
        if (tongSoTinChi > 15) {
            throw new RuntimeException("Lỗi: Vượt quá 15 tín chỉ (hiện tại: " + tongSoTinChi + " tín chỉ)");
        }

        // B5: Nếu tất cả OK -> Tiến hành lưu vào CSDL

        // B5.1: Xóa các đăng ký cũ trong học kỳ này
        List<PhieuDangKy> dsLopCu = phieuDangKyRepo.findBySinhVien_MaSVAndHocKy(sv.getMaSV(), request.getHocKy());
        for(PhieuDangKy pdkCu : dsLopCu) {
            LopHocPhan lopCu = pdkCu.getLopHocPhan();
            if (lopCu != null) {
                lopCu.setSoSvHienTai(lopCu.getSoSvHienTai() - 1);
                lopHocPhanRepo.save(lopCu);
            }
        }
        phieuDangKyRepo.deleteBySinhVienAndHocKy(sv, request.getHocKy());

        // B5.2: Lưu các đăng ký mới
        for (LopHocPhan lopMoi : dsLopMoi) {
            PhieuDangKy pdkMoi = new PhieuDangKy(sv, lopMoi, request.getHocKy());
            phieuDangKyRepo.save(pdkMoi);

            lopMoi.setSoSvHienTai(lopMoi.getSoSvHienTai() + 1);
            lopHocPhanRepo.save(lopMoi);
        }

        // B6: Trả về phiếu đăng ký thành công
        List<PhieuDangKy> dsDangKyMoi = phieuDangKyRepo.findBySinhVien_MaSVAndHocKy(sv.getMaSV(), request.getHocKy());
        return buildRegistrationSlip(sv, request.getHocKy(), dsDangKyMoi);
    }

    // --- Hàm Helper: Xây dựng DTO trả về ---
    private RegistrationSlipDTO buildRegistrationSlip(SinhVien sv, String hocKy, List<PhieuDangKy> dsDangKy) {
        RegistrationSlipDTO slip = new RegistrationSlipDTO();
        slip.setMaSV(sv.getMaSV());
        slip.setTenSV(sv.getTen());
        slip.setKhoaHoc(sv.getKhoa());
        slip.setHocKy(hocKy);

        List<RegistrationSlipDTO.RegisteredCourseDTO> cacMonHocDTO = dsDangKy.stream().map(pdk -> {
            LopHocPhan lhp = pdk.getLopHocPhan();
            MonHoc mh = lhp.getMonHoc();

            return new RegistrationSlipDTO.RegisteredCourseDTO(
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
        slip.setTongSoTinChi(cacMonHocDTO.stream().mapToInt(RegistrationSlipDTO.RegisteredCourseDTO::getSoTinChi).sum());
        slip.setDanhSachDaDangKy(cacMonHocDTO);
        return slip;
    }
}