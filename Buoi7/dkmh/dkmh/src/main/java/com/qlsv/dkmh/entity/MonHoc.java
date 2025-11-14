package com.qlsv.dkmh.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Set;

@Data
@Entity
@Table(name = "monhoc")
public class MonHoc {
    @Id
    private String maMH;
    private String tenMH;
    private int soTinChi;

    @OneToMany(mappedBy = "monHoc")
    private Set<LopHocPhan> cacLopHocPhan;
}