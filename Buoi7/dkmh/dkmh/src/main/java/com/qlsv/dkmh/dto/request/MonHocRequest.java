package com.qlsv.dkmh.dto.request;


import com.qlsv.dkmh.dto.response.MonHocResponse;
import com.qlsv.dkmh.entity.MonHoc;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MonHocRequest {
    @Size(min=6, message="INVALID_MAMONHOC")
     String maMH;
     String tenMH;
     int soTinChi;

    // Phương thức static để chuyển đổi từ Entity sang DTO
    public static MonHocResponse from(MonHoc mh) {
        return new MonHocResponse(
                mh.getMaMH(),
                mh.getTenMH(),
                mh.getSoTinChi()
        );
    }
}
