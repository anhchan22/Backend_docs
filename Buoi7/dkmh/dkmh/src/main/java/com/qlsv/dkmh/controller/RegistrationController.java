package com.qlsv.dkmh.controller;

import com.qlsv.dkmh.dto.request.NopPhieuDangKyRequest;
import com.qlsv.dkmh.dto.request.ThemLopVaoDraftRequest;
import com.qlsv.dkmh.dto.response.ApiResponse;
import com.qlsv.dkmh.dto.response.DraftResponse;
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



//     LẤY draft hiện tại (danh sách lớp đã chọn nhưng chưa nộp)
//     GET http://localhost:8080/api/dang-ky/draft/SV001/2025-1
    @GetMapping("/draft/{maSV}/{hocKy}")
    public ApiResponse<DraftResponse> getDraft(
            @PathVariable String maSV,
            @PathVariable String hocKy) {
        return ApiResponse.<DraftResponse>builder()
                .code(1000)
                .message("Lấy draft thành công")
                .result(registrationService.getDraft(maSV, hocKy))
                .build();
    }

//     THÊM một lớp học phần vào draft (check xung đột thời gian)
//     POST http://localhost:8080/api/dang-ky/draft/add
//     Body: {
//        "maSV": "SV001",
//        "hocKy": "2025-1",
//        "maLop": "IT1110-01"
//     }
    @PostMapping("/draft/add")
    public ApiResponse<DraftResponse> addToDraft(@RequestBody ThemLopVaoDraftRequest request) {
        return ApiResponse.<DraftResponse>builder()
                .code(1000)
                .message("Thêm lớp vào draft thành công")
                .result(registrationService.addToDraft(request.getMaSV(), request.getHocKy(), request.getMaLop()))
                .build();
    }

//     XÓA một lớp khỏi draft
//     DELETE http://localhost:8080/api/dang-ky/draft/remove
//     Body: {
//        "maSV": "SV001",
//        "hocKy": "2025-1",
//        "maLop": "IT1110-01"
//     }
    @DeleteMapping("/draft/remove")
    public ApiResponse<DraftResponse> removeFromDraft(@RequestBody ThemLopVaoDraftRequest request) {
        return ApiResponse.<DraftResponse>builder()
                .code(1000)
                .message("Xóa lớp khỏi draft thành công")
                .result(registrationService.removeFromDraft(request.getMaSV(), request.getHocKy(), request.getMaLop()))
                .build();
    }

//     NỘP phiếu đăng ký (lưu draft vào database, check tín chỉ 10-15)
//     POST http://localhost:8080/api/dang-ky/draft/submit
//     Body: {
//        "maSV": "SV001",
//        "hocKy": "2025-1"
//     }
    @PostMapping("/draft/submit")
    public ApiResponse<PhieuDangKyResponse> submitDraft(@RequestBody NopPhieuDangKyRequest request) {
        return ApiResponse.<PhieuDangKyResponse>builder()
                .code(1000)
                .message("Nộp phiếu đăng ký thành công")
                .result(registrationService.submitDraft(request))
                .build();
    }
}