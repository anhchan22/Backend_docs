package com.qlsv.dkmh.controller;

import com.qlsv.dkmh.dto.request.MonHocRequest;
import com.qlsv.dkmh.dto.response.ApiResponse;
import com.qlsv.dkmh.dto.response.MonHocResponse;
import com.qlsv.dkmh.service.MonHocService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/mon-hoc")
public class MonHocController {

    @Autowired
    private MonHocService monHocService;

    //     Lấy tất cả môn học trong hệ thống.
    @GetMapping("tat-ca-mon-hoc")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<MonHocResponse>> getAllMonHoc() {
        return ApiResponse.<List<MonHocResponse>>builder()
                .code(1000)
                .message("Lấy tất cả môn học thành công")
                .result(monHocService.getAllMonHocDTO())
                .build();
    }

    //     Lấy tất cả môn học được mở trong học kỳ.
    @GetMapping("/mon-hoc")
    public ApiResponse< List<MonHocResponse>> getMonHocHocKy(@RequestParam String hocKy) {
        return ApiResponse.<List<MonHocResponse>>builder()
                .code(1000)
                .message("Lấy môn học trong học kỳ thành công")
                .result(monHocService.getMonHocByHocKyDTO(hocKy))
                .build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ApiResponse<MonHocResponse> createMonHoc(@Valid @RequestBody MonHocRequest request) {
        return ApiResponse.<MonHocResponse>builder()
                .code(1000)
                .message("Taoj môn học thành công")
                .result(monHocService.createMonHoc(request))
                .build();
    }
}


