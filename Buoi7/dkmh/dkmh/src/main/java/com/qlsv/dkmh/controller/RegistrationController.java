package com.qlsv.dkmh.controller;

import com.qlsv.dkmh.dto.request.DangKyRequest;
import com.qlsv.dkmh.dto.response.ApiResponse;
import com.qlsv.dkmh.dto.response.PhieuDangKyResponse;
import com.qlsv.dkmh.service.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/dang-ky")
public class RegistrationController {

    @Autowired
    private RegistrationService registrationService;

//     Lấy phiếu đăng ký (kết quả đã lưu) của sinh viên.
//     GET http://localhost:8080/api/dang-ky/SV001/2025-1
    @GetMapping("/{maSV}/{hocKy}")
    public ApiResponse<PhieuDangKyResponse> getPhieuDangKy(
            @PathVariable String maSV,
            @PathVariable String hocKy) {
        return ApiResponse.<PhieuDangKyResponse>builder()
                .code(1000)
                .message("Lấy phiếu đăng ký thành công")
                .result(registrationService.getPhieuDangKy(maSV, hocKy))
                .build();
    }

//     TẠO phiếu đăng ký mới
//     POST http://localhost:8080/api/dang-ky
//     Body: {
//        "maSV": "SV001",
//        "hocKy": "2025-1",
//        "danhSachMaLop": ["IT1110-01", "IT1120-02"]
//     }
    @PostMapping
    public ApiResponse<PhieuDangKyResponse> createPhieuDangKy(@RequestBody DangKyRequest request) {
        return ApiResponse.<PhieuDangKyResponse>builder()
                .code(1000)
                .message("Tạo phiếu đăng ký thành công")
                .result(registrationService.createPhieuDangKy(request))
                .build();
    }

//     SỬA phiếu đăng ký (cập nhật danh sách lớp học phần)
//     PUT http://localhost:8080/api/dang-ky
//     Body: {
//        "maSV": "SV001",
//        "hocKy": "2025-1",
//        "danhSachMaLop": ["IT1110.1", "MI1110.1", "PE1010.1"]
//     }
    @PutMapping
    public ApiResponse<PhieuDangKyResponse> updatePhieuDangKy(@RequestBody DangKyRequest request) {
       return ApiResponse.<PhieuDangKyResponse>builder()
                .code(1000)
                .message("Cập nhật phiếu đăng ký thành công")
                .result(registrationService.updatePhieuDangKy(request))
                .build();
    }


//     XÓA toàn bộ phiếu đăng ký của sinh viên trong học kỳ
//     DELETE http://localhost:8080/api/dang-ky/SV001/2025-1
    @DeleteMapping("/{maSV}/{hocKy}")
    public ApiResponse<String> deletePhieuDangKy(
            @PathVariable String maSV,
            @PathVariable String hocKy) {
        return ApiResponse.<String>builder()
                .code(1000)
                .message("Xóa phiếu đăng ký thành công")
                .result(registrationService.deletePhieuDangKy(maSV, hocKy))
                .build();
    }


//     XÓA một môn học cụ thể trong phiếu đăng ký
//     DELETE http://localhost:8080/api/dang-ky/SV001/2025-1/IT1110-01
    @DeleteMapping("/{maSV}/{hocKy}/{maLop}")
    public ApiResponse<String> deleteMonHocFromPhieuDangKy(
            @PathVariable String maSV,
            @PathVariable String hocKy,
            @PathVariable String maLop) {
        return ApiResponse.<String>builder()
                .code(1000)
                .message("Xóa môn học khỏi phiếu đăng ký thành công")
                .result(registrationService.deleteMonHocFromPhieuDangKy(maSV, hocKy, maLop))
                .build();
    }
}