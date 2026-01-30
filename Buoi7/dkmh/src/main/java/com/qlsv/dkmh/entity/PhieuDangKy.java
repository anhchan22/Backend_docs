
package com.qlsv.dkmh.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@Entity
@Table(name = "phieudangky")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PhieuDangKy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
     Long id;

     String hocKy;

    @ManyToOne
    @JoinColumn(name = "maSV")
     SinhVien sinhVien;

    @ManyToOne
    @JoinColumn(name = "maLop")
     LopHocPhan lopHocPhan;
}