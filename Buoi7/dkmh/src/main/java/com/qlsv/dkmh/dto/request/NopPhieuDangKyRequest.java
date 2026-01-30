package com.qlsv.dkmh.dto.request;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NopPhieuDangKyRequest {
    String maSV;
    String hocKy;
}

