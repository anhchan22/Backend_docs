package com.qlsv.dkmh.repository;

import com.qlsv.dkmh.entity.LopHocPhan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LopHocPhanRepository extends JpaRepository<LopHocPhan, String> {

    // Tự động tạo query: Tìm các lớp theo mã môn học VÀ học kỳ
    List<LopHocPhan> findByMonHoc_MaMHAndHocKy(String maMH, String hocKy);
}