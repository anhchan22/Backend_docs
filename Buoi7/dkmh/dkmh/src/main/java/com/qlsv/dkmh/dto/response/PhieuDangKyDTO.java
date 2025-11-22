package com.qlsv.dkmh.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

// Dùng để trả về "Phiếu đăng ký" cho client
@Data
public class PhieuDangKyDTO {
    private String maSV;
    private String tenSV;
    private String khoaHoc;
    private String hocKy;
    private List<RegisteredCourseDTO> danhSachDaDangKy;
    private int tongSoTinChi;

    // DTO con để chứa thông tin từng môn
    @Data
    @AllArgsConstructor // Lombok: Tạo constructor có đủ tham số
    public static class RegisteredCourseDTO {
        private String maMH;
        private String tenMH;
        private int soTinChi;
        private String gioHoc;
        private String giangVien;
        private String tenLop;
        private String phongHoc;
    }
}