package com.qlsv.dkmh.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
public class LopHocPhanDTO {
    private String maLop;
    private String tenLop;
    private int soSvToiDa;
    private int soSvHienTai;
    private String khungGioHoc;
    private String phongHoc;
    private String giangVien;
}

