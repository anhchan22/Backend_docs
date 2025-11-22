package com.qlsv.dkmh.repository;


import com.qlsv.dkmh.entity.LopHocPhan;
import com.qlsv.dkmh.entity.PhieuDangKy;
import com.qlsv.dkmh.entity.SinhVien;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PhieuDangKyRepository extends JpaRepository<PhieuDangKy, Long> {

    //Tìm tất cả đăng ký của 1 sinh viên trong 1 học kỳ
    List<PhieuDangKy> findBySinhVien_MaSVAndHocKy(String maSV, String hocKy);


    @Modifying
    @Query("DELETE FROM PhieuDangKy p WHERE p.sinhVien = :sinhVien AND p.hocKy = :hocKy")
    void deleteBySinhVienAndHocKy(SinhVien sinhVien, String hocKy);

    //Kiểm tra xem sinh viên đã đăng ký lớp học phần chưa
    boolean existsBySinhVienAndLopHocPhanAndHocKy(SinhVien sinhVien, LopHocPhan lopHocPhan, String hocKy);

    //Tìm phiếu đăng ký cụ thể của sinh viên cho một lớp học phần
    @Query("SELECT p FROM PhieuDangKy p WHERE p.sinhVien.maSV = :maSV AND p.lopHocPhan.maLop = :maLop AND p.hocKy = :hocKy")
    Optional<PhieuDangKy> findByMaSVAndMaLopAndHocKy(@Param("maSV") String maSV,
                                                      @Param("maLop") String maLop,
                                                      @Param("hocKy") String hocKy);

    //Đếm số lượng đăng ký của sinh viên trong học kỳ
    @Query(value = "SELECT COUNT(*) FROM phieudangky WHERE masv = :maSV AND hoc_ky = :hocKy", nativeQuery = true)
    int countRegistrationsByMaSVAndHocKy(@Param("maSV") String maSV, @Param("hocKy") String hocKy);

    //Xóa một phiếu đăng ký cụ thể
    @Modifying
    @Query("DELETE FROM PhieuDangKy p WHERE p.sinhVien.maSV = :maSV AND p.lopHocPhan.maLop = :maLop AND p.hocKy = :hocKy")
    void deleteByMaSVAndMaLopAndHocKy(@Param("maSV") String maSV,
                                      @Param("maLop") String maLop,
                                      @Param("hocKy") String hocKy);

    //Lấy danh sách mã lớp đã đăng ký của sinh viên
    @Query(value = "SELECT ma_lop FROM phieudangky WHERE masv = :maSV AND hoc_ky = :hocKy", nativeQuery = true)
    List<String> findMaLopByMaSVAndHocKy(@Param("maSV") String maSV, @Param("hocKy") String hocKy);
}