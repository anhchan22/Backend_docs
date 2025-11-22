package com.qlsv.dkmh.controller;

import com.qlsv.dkmh.dto.request.DangKyRequest;
import com.qlsv.dkmh.dto.response.LopHocPhanDTO;
import com.qlsv.dkmh.dto.response.MonHocDTO;
import com.qlsv.dkmh.dto.response.PhieuDangKyDTO;
import com.qlsv.dkmh.service.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dang-ky")
public class RegistrationController {

    @Autowired
    private RegistrationService registrationService;

//     Lấy tất cả môn học trong hệ thống.
    @GetMapping("tat-ca-mon-hoc")
    public List<MonHocDTO> getAllMonHoc() {
        return registrationService.getAllMonHocDTO();
    }


//     Lấy tất cả môn học được mở trong học kỳ.
//     GET http://localhost:8080/api/dang-ky/mon-hoc?hocKy=2025-1
    @GetMapping("/mon-hoc")
    public List<MonHocDTO> getMonHocHocKy(@RequestParam String hocKy) {
        return registrationService.getMonHocByHocKyDTO(hocKy);
    }

//     Lấy các lớp học phần của 1 môn học trong học kỳ.
//     GET http://localhost:8080/api/dang-ky/lop-hoc-phan?maMH=IT1110&hocKy=2025-1
    @GetMapping("/lop-hoc-phan")
    public ResponseEntity<List<LopHocPhanDTO>> getLopHocPhan(
            @RequestParam String maMH,
            @RequestParam String hocKy) {
        return ResponseEntity.ok(registrationService.getLopHocPhanByMonHoc(maMH, hocKy));
    }

//     Lấy phiếu đăng ký (kết quả đã lưu) của sinh viên.
//     GET http://localhost:8080/api/dang-ky/SV001/2025-1
    @GetMapping("/{maSV}/{hocKy}")
    public ResponseEntity<PhieuDangKyDTO> getPhieuDangKy(
            @PathVariable String maSV,
            @PathVariable String hocKy) {
        PhieuDangKyDTO phieu = registrationService.getPhieuDangKy(maSV, hocKy);
        return ResponseEntity.ok(phieu);
    }

//     TẠO phiếu đăng ký mới
//     POST http://localhost:8080/api/dang-ky
//     Body: {
//        "maSV": "SV001",
//        "hocKy": "2025-1",
//        "danhSachMaLop": ["IT1110-01", "IT1120-02"]
//     }
    @PostMapping
    public ResponseEntity<PhieuDangKyDTO> createPhieuDangKy(@RequestBody DangKyRequest request) {
        PhieuDangKyDTO phieu = registrationService.createPhieuDangKy(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(phieu);
    }

//     SỬA phiếu đăng ký (cập nhật danh sách lớp học phần)
//     PUT http://localhost:8080/api/dang-ky
//     Body: {
//        "maSV": "SV001",
//        "hocKy": "2025-1",
//        "danhSachMaLop": ["IT1110-01", "IT1130-01", "IT1140-01"]
//     }
    @PutMapping
    public ResponseEntity<?> updatePhieuDangKy(@RequestBody DangKyRequest request) {
        PhieuDangKyDTO phieu = registrationService.updatePhieuDangKy(request);
        return ResponseEntity.ok(phieu);
    }


//     XÓA toàn bộ phiếu đăng ký của sinh viên trong học kỳ
//     DELETE http://localhost:8080/api/dang-ky/SV001/2025-1
    @DeleteMapping("/{maSV}/{hocKy}")
    public String deletePhieuDangKy(
            @PathVariable String maSV,
            @PathVariable String hocKy) {
        return registrationService.deletePhieuDangKy(maSV, hocKy);
    }


//     XÓA một môn học cụ thể trong phiếu đăng ký
//     DELETE http://localhost:8080/api/dang-ky/SV001/2025-1/IT1110-01
    @DeleteMapping("/{maSV}/{hocKy}/{maLop}")
    public String deleteMonHocFromPhieuDangKy(
            @PathVariable String maSV,
            @PathVariable String hocKy,
            @PathVariable String maLop) {
        return registrationService.deleteMonHocFromPhieuDangKy(maSV, maLop, hocKy);
    }
}