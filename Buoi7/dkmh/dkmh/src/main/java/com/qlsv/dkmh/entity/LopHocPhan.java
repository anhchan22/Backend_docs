package com.qlsv.dkmh.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Set;

@Data
@Entity
@Table(name = "lophocphan")
public class LopHocPhan {
    @Id
    private String maLop;
    private String tenLop;
    private int soSvToiDa;
    private int soSvHienTai = 0; // Luôn khởi tạo bằng 0
    private String phongHoc;
    private String khungGioHoc; // Ví dụ: "T2(1-3)"
    private String hocKy; // Ví dụ: "2025-1"
    private String giangVien; // Thêm theo yêu cầu phiếu đăng ký

    @ManyToOne
    @JoinColumn(name = "maMH") // Khóa ngoại
    private MonHoc monHoc;

    @OneToMany(mappedBy = "lopHocPhan")
    private Set<PhieuDangKy> cacPhieuDangKy;
}