package com.qlsv.dkmh.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DraftResponse {
    String maSV;
    String hocKy;
    List<LopHocPhanTrongDraft> danhSachLopDaChon = new ArrayList<>();
    int tongSoTinChi = 0;
    boolean duDieuKienNop = false;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class LopHocPhanTrongDraft {
        String maLop;
        String tenLop;
        String maMH;
        String tenMH;
        int soTinChi;
        String khungGioHoc;
        String giangVien;
        String phongHoc;
    }
}

