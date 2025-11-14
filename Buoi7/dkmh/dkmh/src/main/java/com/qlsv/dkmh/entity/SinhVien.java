package com.qlsv.dkmh.entity;
import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;
import java.util.Set;

@Data
@Entity
@Table(name = "sinhvien")
public class SinhVien {
    @Id
    private String maSV;
    private String matKhau;
    private String ten;
    private Date ngaySinh;
    private String khoa;
    private String queQuan;
    private String diaChi;

    @OneToMany(mappedBy = "sinhVien")
    private Set<PhieuDangKy> cacPhieuDangKy;
}