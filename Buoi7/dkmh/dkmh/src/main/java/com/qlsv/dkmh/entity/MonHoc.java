package com.qlsv.dkmh.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Data
@Entity
@Table(name = "monhoc")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MonHoc {
    @Id
     String maMH;
     String tenMH;
     int soTinChi;

    @OneToMany(mappedBy = "monHoc")
     Set<LopHocPhan> cacLopHocPhan;
}