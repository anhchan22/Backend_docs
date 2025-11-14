package com.qlsv.dkmh.controller;

import com.qlsv.dkmh.dto.LopHocPhanDTO;
import com.qlsv.dkmh.dto.MonHocDTO;
import com.qlsv.dkmh.dto.RegistrationRequest;
import com.qlsv.dkmh.dto.RegistrationSlipDTO;
import com.qlsv.dkmh.entity.LopHocPhan;
import com.qlsv.dkmh.entity.MonHoc;
import com.qlsv.dkmh.service.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/dang-ky") // Tiền tố chung cho tất cả API
public class RegistrationController {

    @Autowired
    private RegistrationService registrationService;

    @GetMapping("tat-ca-mon-hoc")
    public List<MonHocDTO> getAllMonHoc() {
        return registrationService.getAllMonHoc().stream()
                .map(mh -> new MonHocDTO(mh.getMaMH(), mh.getTenMH(), mh.getSoTinChi()))
                .collect(Collectors.toList());
    }

    /**
     * API 1: Lấy tất cả môn học được mở trong học kỳ.
     * Ví dụ: GET http://localhost:8080/api/dang-ky/mon-hoc?hocKy=2025-1
     */
    @GetMapping("/mon-hoc")
    public ResponseEntity<List<MonHocDTO>> getMonHocHocKy(@RequestParam String hocKy) {
        List<MonHoc> dsMonHoc = registrationService.getMonHocByHocKy(hocKy);
        // Chuyển đổi sang DTO
        List<MonHocDTO> dsMonHocDTO = dsMonHoc.stream()
                .map(mh -> new MonHocDTO(mh.getMaMH(), mh.getTenMH(), mh.getSoTinChi()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(dsMonHocDTO);
    }

    /**
     * API 2: Lấy các lớp học phần của 1 môn học trong học kỳ.
     * Ví dụ: GET http://localhost:8080/api/dang-ky/lop-hoc-phan?maMH=IT1110&hocKy=2025-1
     */
    @GetMapping("/lop-hoc-phan")
    public ResponseEntity<List<LopHocPhanDTO>> getLopHocPhan(
            @RequestParam String maMH,
            @RequestParam String hocKy) {
        List<LopHocPhan> dsLop = registrationService.getLopHocPhanByMonHoc(maMH, hocKy);
        // Chuyển đổi sang DTO để tránh lỗi tuần hoàn
        List<LopHocPhanDTO> dsLopDTO = dsLop.stream()
                .map(lhp -> new LopHocPhanDTO(
                        lhp.getMaLop(),
                        lhp.getTenLop(),
                        lhp.getSoSvToiDa(),
                        lhp.getSoSvHienTai(),
                        lhp.getKhungGioHoc(),
                        lhp.getPhongHoc(),
                        lhp.getGiangVien()
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(dsLopDTO);
    }

    /**
     * API 3: Lấy phiếu đăng ký (kết quả đã lưu) của sinh viên.
     * Ví dụ: GET http://localhost:8080/api/dang-ky/SV001/2025-1
     */
    @GetMapping("/{maSV}/{hocKy}")
    public ResponseEntity<RegistrationSlipDTO> getPhieuDangKy(
            @PathVariable String maSV,
            @PathVariable String hocKy) {
        RegistrationSlipDTO phieu = registrationService.getPhieuDangKy(maSV, hocKy);
        return ResponseEntity.ok(phieu);
    }

    /**
     * API 4: Gửi yêu cầu đăng ký (API chính).
     * Ví dụ: POST http://localhost:8080/api/dang-ky
     * Body (JSON):
     * {
     * "maSV": "SV001",
     * "hocKy": "2025-1",
     * "danhSachMaLop": ["IT1110.1", "MA102.3", "PE103.5"]
     * }
     */
    @PostMapping
    public ResponseEntity<?> submitRegistration(@RequestBody RegistrationRequest request) {
        try {
            // Gọi service để xử lý tất cả các ràng buộc
            RegistrationSlipDTO phieuThanhCong = registrationService.registerCourses(request);

            // Trả về phiếu đăng ký và mã 201 (Created)
            return new ResponseEntity<>(phieuThanhCong, HttpStatus.CREATED);

        } catch (RuntimeException e) {
            // Nếu Service ném lỗi (vd: trùng lịch), trả về thông báo lỗi
            // và mã 400 (Bad Request)
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}