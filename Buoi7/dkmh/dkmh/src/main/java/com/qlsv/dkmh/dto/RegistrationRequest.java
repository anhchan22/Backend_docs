package com.qlsv.dkmh.dto;

import lombok.Data;
import java.util.List;

// Dùng để nhận yêu cầu POST từ client
@Data
public class RegistrationRequest {
    private String maSV;
    private String hocKy;
    // Client gửi lên danh sách mã lớp họ chọn
    private List<String> danhSachMaLop;
}