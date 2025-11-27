package com.qlsv.dkmh.dto.response;

import com.qlsv.dkmh.entity.MonHoc;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MonHocResponse {
     String maMH;
     String tenMH;
     int soTinChi;

    public MonHocResponse(String maMH, String tenMH, int soTinChi) {
        this.maMH = maMH;
        this.tenMH = tenMH;
        this.soTinChi = soTinChi;
    }
}

