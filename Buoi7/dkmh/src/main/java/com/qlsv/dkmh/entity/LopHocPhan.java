package com.qlsv.dkmh.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Data
@Entity
@Table(name = "lophocphan")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LopHocPhan {
    @Id
     String maLop;
     String tenLop;
     int soSvToiDa;
     int soSvHienTai = 0;
     String phongHoc;
     String khungGioHoc;
     String hocKy;
     String giangVien;

    @ManyToOne
    @JoinColumn(name = "maMH")
     MonHoc monHoc;

    @OneToMany(mappedBy = "lopHocPhan")
     Set<PhieuDangKy> cacPhieuDangKy;
}