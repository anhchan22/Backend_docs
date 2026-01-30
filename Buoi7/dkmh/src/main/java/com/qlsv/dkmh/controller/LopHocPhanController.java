package com.qlsv.dkmh.controller;

import com.qlsv.dkmh.dto.request.LopHocPhanRequest;
import com.qlsv.dkmh.dto.response.ApiResponse;
import com.qlsv.dkmh.dto.response.LopHocPhanResponse;
import com.qlsv.dkmh.service.LopHocPhanService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lop-hoc-phan")
public class LopHocPhanController {

    @Autowired
    private LopHocPhanService lopHocPhanService;

    //     Lấy các lớp học phần của 1 môn học trong học kỳ.
    @GetMapping()
    public ApiResponse<List<LopHocPhanResponse>> getLopHocPhan(
            @RequestParam String maMH,
            @RequestParam String hocKy) {
        return ApiResponse.<List<LopHocPhanResponse>>builder()
                .code(1000)
                .message("Lấy lớp học phần thành công")
                .result(lopHocPhanService.getLopHocPhanByMonHoc(maMH, hocKy))
                .build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ApiResponse<LopHocPhanResponse> createLopHocPhan(@Valid @RequestBody LopHocPhanRequest request) {
        return ApiResponse.<LopHocPhanResponse>builder()
                .code(1000)
                .message("Tạo lớp học phần thành công")
                .result(lopHocPhanService.createLopHocPhan(request))
                .build();
    }

    @GetMapping("/all-sorted")
    public ApiResponse<Page<LopHocPhanResponse>> getAllLopHocPhanSorted(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        return ApiResponse.<Page<LopHocPhanResponse>>builder()
                .code(1000)
                .message("Lấy danh sách lớp học phần thành công")
                .result(lopHocPhanService.getAllLopHocPhanSortedByMaMHDesc(page, size))
                .build();
    }
}
