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
    @Query("select l from LopHocPhan l where l.monHoc.maMH = :maMH and l.hocKy = :hocKy")
    List<LopHocPhan> findByMonHocAndHocKy(@Param("maMH") String maMH, @Param("hocKy") String hocKy);

    //Tăng số sinh viên hiện tại
    @Modifying
    @Query(value = "update lophocphan set so_sv_hien_tai = so_sv_hien_tai + 1 where ma_lop = :maLop", nativeQuery = true)
    void incrementSoSvHienTai(@Param("maLop") String maLop);

    //Giảm số sinh viên hiện tại
    @Modifying
    @Query(value = "update lophocphan set so_sv_hien_tai = so_sv_hien_tai - 1 where ma_lop = :maLop and so_sv_hien_tai > 0", nativeQuery = true)
    void decrementSoSvHienTai(@Param("maLop") String maLop);
}