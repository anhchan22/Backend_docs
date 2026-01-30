package com.qlsv.dkmh.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;


@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class LopHocPhanRequest {
    @Size(min = 6, message = "INVALID_MALOP")
     String maLop;
     String tenLop;

    @Max(value = 50, message = "INVALID_SOSV_TOIDA")
     int soSvToiDa;

     String khungGioHoc;
     String phongHoc;
     String giangVien;
     String hocKy;

    @NotBlank(message = "INVALID_MAMONHOC")
     String maMH;
}


