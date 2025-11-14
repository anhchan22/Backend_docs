package com.qlsv.dkmh.dto;

import lombok.Data;

@Data
public class MonHocDTO {
    private String maMH;
    private String tenMH;
    private int soTinChi;

    public MonHocDTO(String maMH, String tenMH, int soTinChi) {
        this.maMH = maMH;
        this.tenMH = tenMH;
        this.soTinChi = soTinChi;
    }
}

