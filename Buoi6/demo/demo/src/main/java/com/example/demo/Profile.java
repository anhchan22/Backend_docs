package com.example.demo;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
//@AllArgsConstructor

public class Profile {
//    private String ten, maSV, lop, email, diaChi, tinhTrangMQH;
    private int tuoi;
//    private List<String> soThich;
    public Profile(int tuoi){
        this.tuoi = tuoi;
    }

//        public Profile(String ten, String maSV, String lop, String email, String diaChi, String tinhTrangMQH, int tuoi, List<String> soThich) {
//        this.ten = ten;
//        this.maSV = maSV;
//        this.lop = lop;
//        this.email = email;
//        this.diaChi = diaChi;
//        this.tinhTrangMQH = tinhTrangMQH;
//        this.tuoi = tuoi;
//        this.soThich = soThich;
//    }
}
