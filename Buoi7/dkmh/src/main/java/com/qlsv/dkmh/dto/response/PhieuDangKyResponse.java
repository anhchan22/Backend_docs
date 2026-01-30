package com.qlsv.dkmh.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PhieuDangKyResponse {
     String maSV;
     String tenSV;
     String khoaHoc;
     String hocKy;
     List<MonHocDaDangKyResponse> danhSachMHDaDangKy;
     int tongSoTinChi;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class MonHocDaDangKyResponse {
        String maMH;
        String tenMH;
        int soTinChi;
        String gioHoc;
        String giangVien;
        String tenLop;
        String phongHoc;
    }
}