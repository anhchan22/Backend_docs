package com.qlsv.dkmh.dto.request;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;

// Dùng để nhận yêu cầu POST từ client
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DangKyRequest {
     String maSV;
     String hocKy;
    // Client gửi lên danh sách mã lớp họ chọn
     List<String> danhSachMaLop;
}