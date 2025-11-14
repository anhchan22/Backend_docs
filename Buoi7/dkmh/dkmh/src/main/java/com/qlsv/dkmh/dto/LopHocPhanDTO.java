package com.qlsv.dkmh.dto;

import lombok.Data;

/**
 * Data Transfer Object cho Lớp Học Phần.
 * Chỉ chứa các thông tin cần thiết cho client, tránh lỗi tuần tự hóa.
 */
@Data
public class LopHocPhanDTO {
    private String maLop;
    private String tenLop;
    private int soSvToiDa;
    private int soSvHienTai;
    private String khungGioHoc;
    private String phongHoc;
    private String giangVien;

    // Constructor đầy đủ
    public LopHocPhanDTO(String maLop, String tenLop, int soSvToiDa, int soSvHienTai, String khungGioHoc, String phongHoc, String giaoVien) {
        this.maLop = maLop;
        this.tenLop = tenLop;
        this.soSvToiDa = soSvToiDa;
        this.soSvHienTai = soSvHienTai;
        this.khungGioHoc = khungGioHoc;
        this.phongHoc = phongHoc;
        this.giangVien = giaoVien;
    }

}

