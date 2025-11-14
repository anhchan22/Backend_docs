package com.qlsv.dkmh.dto;

/**
 * Data Transfer Object cho Môn học.
 * Chỉ chứa các thông tin cần thiết cho client, tránh lỗi tuần tự hóa.
 */
public class MonHocDTO {
    private String maMH;
    private String tenMH;
    private int soTinChi;

    public MonHocDTO(String maMH, String tenMH, int soTinChi) {
        this.maMH = maMH;
        this.tenMH = tenMH;
        this.soTinChi = soTinChi;
    }

    // Constructor mặc định cho Jackson
    public MonHocDTO() {}

    // Getters and Setters
    public String getMaMH() {
        return maMH;
    }

    public void setMaMH(String maMH) {
        this.maMH = maMH;
    }

    public String getTenMH() {
        return tenMH;
    }

    public void setTenMH(String tenMH) {
        this.tenMH = tenMH;
    }

    public int getSoTinChi() {
        return soTinChi;
    }

    public void setSoTinChi(int soTinChi) {
        this.soTinChi = soTinChi;
    }
}

