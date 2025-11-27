package com.qlsv.dkmh.entity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.Date;
import java.util.Set;

@Data
@Entity
@Table(name = "sinhvien")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SinhVien {
    @Id
     String maSV;
     String matKhau;
     String ten;
     Date ngaySinh;
     String khoa;
     String queQuan;
     String diaChi;

    @OneToMany(mappedBy = "sinhVien")
     Set<PhieuDangKy> cacPhieuDangKy;
}