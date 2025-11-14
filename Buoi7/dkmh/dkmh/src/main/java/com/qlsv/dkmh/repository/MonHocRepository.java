package com.qlsv.dkmh.repository;


import com.qlsv.dkmh.entity.MonHoc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MonHocRepository extends JpaRepository<MonHoc, String> {

    // Dùng @Query để tìm các môn học có mở lớp trong học kỳ
    @Query("SELECT DISTINCT m FROM MonHoc m JOIN m.cacLopHocPhan l WHERE l.hocKy = :hocKy")
    List<MonHoc> findMonHocByHocKy(String hocKy);
}