package com.qlsv.dkmh.dto.request;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ThemLopVaoDraftRequest {
    String maSV;
    String hocKy;
    String maLop; // Thêm từng lớp một
}

