package com.qlsv.dkmh;


import com.qlsv.dkmh.entity.LopHocPhan;
import com.qlsv.dkmh.entity.MonHoc;
import com.qlsv.dkmh.entity.PhieuDangKy;
import com.qlsv.dkmh.entity.SinhVien;
import com.qlsv.dkmh.repository.LopHocPhanRepository;
import com.qlsv.dkmh.repository.MonHocRepository;
import com.qlsv.dkmh.repository.PhieuDangKyRepository;
import com.qlsv.dkmh.repository.SinhVienRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.List;

@Component // Báo cho Spring biết đây là một Bean, hãy quản lý nó
public class DataInitializer implements CommandLineRunner {

    // Tiêm (Inject) tất cả các repository
    @Autowired private SinhVienRepository sinhVienRepo;
    @Autowired private MonHocRepository monHocRepo;
    @Autowired private LopHocPhanRepository lopHocPhanRepo;
    @Autowired private PhieuDangKyRepository phieuDangKyRepo;

    @Override
    public void run(String... args) throws Exception {
        // Chỉ chạy nếu bảng sinhvien chưa có dữ liệu
        if (sinhVienRepo.count() == 0) {
            System.out.println("--- Bắt đầu thêm dữ liệu mẫu ---");

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            // 1. THÊM SINH VIÊN
            SinhVien sv1 = new SinhVien();
            sv1.setMaSV("SV001");
            sv1.setMatKhau("pass123");
            sv1.setTen("Nguyễn Văn An");
            sv1.setNgaySinh(sdf.parse("2004-10-20"));
            sv1.setKhoa("K18");
            sv1.setQueQuan("Hà Nội");
            sv1.setDiaChi("123 Đường Cầu Giấy");

            SinhVien sv2 = new SinhVien();
            sv2.setMaSV("SV002");
            sv2.setMatKhau("pass456");
            sv2.setTen("Trần Thị Bình");
            sv2.setNgaySinh(sdf.parse("2004-05-15"));
            sv2.setKhoa("K18");
            sv2.setQueQuan("Hải Phòng");
            sv2.setDiaChi("456 Đường Lạch Tray");

            // Lưu sinh viên vào CSDL
            sinhVienRepo.saveAll(List.of(sv1, sv2));

            // 2. THÊM MÔN HỌC
            MonHoc mh1 = new MonHoc();
            mh1.setMaMH("IT1110");
            mh1.setTenMH("Lập trình C++");
            mh1.setSoTinChi(3);

            MonHoc mh2 = new MonHoc();
            mh2.setMaMH("MI1110");
            mh2.setTenMH("Giải tích 1");
            mh2.setSoTinChi(3);

            MonHoc mh3 = new MonHoc();
            mh3.setMaMH("PE1010");
            mh3.setTenMH("Giáo dục thể chất 1");
            mh3.setSoTinChi(1);

            MonHoc mh4 = new MonHoc();
            mh4.setMaMH("IT2020");
            mh4.setTenMH("Cấu trúc dữ liệu & Giải thuật");
            mh4.setSoTinChi(4);

            MonHoc mh5 = new MonHoc();
            mh5.setMaMH("SS1010");
            mh5.setTenMH("Tư tưởng Hồ Chí Minh");
            mh5.setSoTinChi(2);

            MonHoc mh6 = new MonHoc();
            mh6.setMaMH("FL1010");
            mh6.setTenMH("Tiếng Anh 1");
            mh6.setSoTinChi(3);

            // Lưu môn học
            monHocRepo.saveAll(List.of(mh1, mh2, mh3, mh4, mh5, mh6));

            // 3. THÊM LỚP HỌC PHẦN (Phải setMonHoc)
            LopHocPhan lhp1 = new LopHocPhan();
            lhp1.setMaLop("IT1110.1");
            lhp1.setTenLop("Lớp (01)");
            lhp1.setSoSvToiDa(50);
            lhp1.setSoSvHienTai(0);
            lhp1.setPhongHoc("D5-301");
            lhp1.setKhungGioHoc("T2(1-3)");
            lhp1.setHocKy("2025-1");
            lhp1.setGiangVien("Nguyễn Văn A");
            lhp1.setMonHoc(mh1); // <-- Quan trọng: set đối tượng MonHoc

            LopHocPhan lhp2 = new LopHocPhan();
            lhp2.setMaLop("IT1110.2");
            lhp2.setTenLop("Lớp (02)");
            lhp2.setSoSvToiDa(50);
            lhp2.setSoSvHienTai(1); // SV002 đã đăng ký
            lhp2.setPhongHoc("D5-302");
            lhp2.setKhungGioHoc("T3(1-3)");
            lhp2.setHocKy("2025-1");
            lhp2.setGiangVien("Trần Văn B");
            lhp2.setMonHoc(mh1); // <-- Quan trọng

            LopHocPhan lhp3 = new LopHocPhan();
            lhp3.setMaLop("MI1110.1");
            lhp3.setTenLop("Lớp (01)");
            lhp3.setSoSvToiDa(100);
            lhp3.setSoSvHienTai(0);
            lhp3.setPhongHoc("D9-101");
            lhp3.setKhungGioHoc("T2(1-3)"); // Trùng lịch với lhp1
            lhp3.setHocKy("2025-1");
            lhp3.setGiangVien("Lê Thị C");
            lhp3.setMonHoc(mh2);

            LopHocPhan lhp4 = new LopHocPhan();
            lhp4.setMaLop("MI1110.2");
            lhp4.setTenLop("Lớp (02)");
            lhp4.setSoSvToiDa(100);
            lhp4.setSoSvHienTai(1); // SV002 đã đăng ký
            lhp4.setPhongHoc("D9-102");
            lhp4.setKhungGioHoc("T3(4-6)");
            lhp4.setHocKy("2025-1");
            lhp4.setGiangVien("Lê Thị C");
            lhp4.setMonHoc(mh2);

            LopHocPhan lhp5 = new LopHocPhan();
            lhp5.setMaLop("IT2020.1");
            lhp5.setTenLop("Lớp (01)");
            lhp5.setSoSvToiDa(50);
            lhp5.setSoSvHienTai(0);
            lhp5.setPhongHoc("D5-303");
            lhp5.setKhungGioHoc("T4(7-9)");
            lhp5.setHocKy("2025-1");
            lhp5.setGiangVien("Phạm Hùng D");
            lhp5.setMonHoc(mh4);

            LopHocPhan lhp6 = new LopHocPhan();
            lhp6.setMaLop("FL1010.1");
            lhp6.setTenLop("Lớp (01)");
            lhp6.setSoSvToiDa(70);
            lhp6.setSoSvHienTai(0);
            lhp6.setPhongHoc("C1-202");
            lhp6.setKhungGioHoc("T5(1-3)");
            lhp6.setHocKy("2025-1");
            lhp6.setGiangVien("Lý Thu E");
            lhp6.setMonHoc(mh6);

            LopHocPhan lhp7 = new LopHocPhan();
            lhp7.setMaLop("SS1010.1");
            lhp7.setTenLop("Lớp (01)");
            lhp7.setSoSvToiDa(150);
            lhp7.setSoSvHienTai(0);
            lhp7.setPhongHoc("H1-101");
            lhp7.setKhungGioHoc("T6(4-6)");
            lhp7.setHocKy("2025-1");
            lhp7.setGiangVien("Hoàng Văn F");
            lhp7.setMonHoc(mh5);

            LopHocPhan lhp8 = new LopHocPhan();
            lhp8.setMaLop("PE1010.1");
            lhp8.setTenLop("Lớp (01)");
            lhp8.setSoSvToiDa(30);
            lhp8.setSoSvHienTai(30); // Lớp đầy
            lhp8.setPhongHoc("Sân Vận Động");
            lhp8.setKhungGioHoc("T7(7-9)");
            lhp8.setHocKy("2025-1");
            lhp8.setGiangVien("Vũ Tiến G");
            lhp8.setMonHoc(mh3);

            // Lưu các lớp học phần
            lopHocPhanRepo.saveAll(List.of(lhp1, lhp2, lhp3, lhp4, lhp5, lhp6, lhp7, lhp8));

            // 4. THÊM PHIẾU ĐĂNG KÝ (CHO SV002)
            // Dùng constructor tiện lợi đã tạo trong PhieuDangKy.java
            PhieuDangKy pdk1 = new PhieuDangKy(sv2, lhp2, "2025-1");
            PhieuDangKy pdk2 = new PhieuDangKy(sv2, lhp4, "2025-1");

            // Lưu phiếu đăng ký
            phieuDangKyRepo.saveAll(List.of(pdk1, pdk2));

            System.out.println("--- Thêm dữ liệu mẫu thành công ---");
        } else {
            System.out.println("--- Dữ liệu đã tồn tại, không thêm mẫu ---");
        }
    }
}