package com.qlsv.dkmh.repository;

import com.qlsv.dkmh.entity.LopHocPhan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LopHocPhanRepository extends JpaRepository<LopHocPhan, String> {

    //Tìm các lớp theo mã môn học va học kỳ
    @Query("SELECT l FROM LopHocPhan l WHERE l.monHoc.maMH = :maMH AND l.hocKy = :hocKy")
    List<LopHocPhan> findByMonHocAndHocKy(@Param("maMH") String maMH, @Param("hocKy") String hocKy);

    //Tăng số sinh viên hiện tại
    @Modifying
    @Query(value = "UPDATE lophocphan SET so_sv_hien_tai = so_sv_hien_tai + 1 WHERE ma_lop = :maLop", nativeQuery = true)
    void incrementSoSvHienTai(@Param("maLop") String maLop);

    //Giảm số sinh viên hiện tại
    @Modifying
    @Query(value = "UPDATE lophocphan SET so_sv_hien_tai = so_sv_hien_tai - 1 WHERE ma_lop = :maLop AND so_sv_hien_tai > 0", nativeQuery = true)
    void decrementSoSvHienTai(@Param("maLop") String maLop);
}