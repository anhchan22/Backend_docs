package com.qlsv.dkmh.repository;


import com.qlsv.dkmh.entity.MonHoc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MonHocRepository extends JpaRepository<MonHoc, String> {
    List<MonHoc> findAllBy();
    boolean existsById(String id);

    //tìm các môn học có mở lớp trong học kỳ
    @Query("select distinct m from MonHoc m join m.cacLopHocPhan l where l.hocKy = :hocKy")
    List<MonHoc> findMonHocByHocKy(String hocKy);
}