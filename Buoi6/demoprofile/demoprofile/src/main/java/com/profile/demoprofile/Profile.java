package com.profile.demoprofile;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class Profile {
    private String ten, maSV, lop, email, diaChi, tinhTrangMQH;
    private int tuoi;
    private List<String> soThich;

    // Thêm constructor rõ ràng vì Lombok đôi khi không được xử lý trong môi trường build
//    public Profile() {
//    }

//    public Profile(String ten, String maSV, String lop, String email, String diaChi, String tinhTrangMQH, int tuoi, List<String> soThich) {
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
