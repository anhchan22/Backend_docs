
package com.qlsv.dkmh.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor // Lombok: Cần constructor rỗng
@Entity
@Table(name = "phieudangky")
public class PhieuDangKy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String hocKy;

    @ManyToOne
    @JoinColumn(name = "maSV")
    private SinhVien sinhVien;

    @ManyToOne
    @JoinColumn(name = "maLop")
    private LopHocPhan lopHocPhan;

    public PhieuDangKy(SinhVien sv, LopHocPhan lhp, String hocKy) {
        this.sinhVien = sv;
        this.lopHocPhan = lhp;
        this.hocKy = hocKy;
    }
}