package com.qlsv.dkmh.repository;


import com.qlsv.dkmh.entity.PhieuDangKy;
import com.qlsv.dkmh.entity.SinhVien;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PhieuDangKyRepository extends JpaRepository<PhieuDangKy, Long> {

    // Tìm tất cả đăng ký của 1 sinh viên trong 1 học kỳ
    List<PhieuDangKy> findBySinhVien_MaSVAndHocKy(String maSV, String hocKy);

    // Dùng @Query và @Modifying để viết lệnh XÓA
    @Modifying // Báo cho Spring biết đây là lệnh thay đổi dữ liệu (DELETE/UPDATE)
    @Query("DELETE FROM PhieuDangKy p WHERE p.sinhVien = :sinhVien AND p.hocKy = :hocKy")
    void deleteBySinhVienAndHocKy(SinhVien sinhVien, String hocKy);
}