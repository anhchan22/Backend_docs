package com.qlsv.dkmh.mapper;


import com.qlsv.dkmh.dto.response.PhieuDangKyResponse;
import com.qlsv.dkmh.entity.PhieuDangKy;
import com.qlsv.dkmh.entity.SinhVien;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PhieuDangKyMapper {

    // Map từ PhieuDangKy entity sang MonHocDaDangKyResponse
    @Mapping(source = "lopHocPhan.monHoc.maMH", target = "maMH")
    @Mapping(source = "lopHocPhan.monHoc.tenMH", target = "tenMH")
    @Mapping(source = "lopHocPhan.monHoc.soTinChi", target = "soTinChi")
    @Mapping(source = "lopHocPhan.khungGioHoc", target = "gioHoc")
    @Mapping(source = "lopHocPhan.giangVien", target = "giangVien")
    @Mapping(source = "lopHocPhan.tenLop", target = "tenLop")
    @Mapping(source = "lopHocPhan.phongHoc", target = "phongHoc")
    PhieuDangKyResponse.MonHocDaDangKyResponse toMonHocDaDangKyResponse(PhieuDangKy phieuDangKy);

    // Map danh sách PhieuDangKy sang danh sách MonHocDaDangKyResponse
    List<PhieuDangKyResponse.MonHocDaDangKyResponse> toMonHocDaDangKyResponseList(List<PhieuDangKy> phieuDangKyList);

    // Map thông tin sinh viên cơ bản
    @Mapping(source = "maSV", target = "maSV")
    @Mapping(source = "ten", target = "tenSV")
    @Mapping(source = "khoa", target = "khoaHoc")
    PhieuDangKyResponse toPhieuDangKyResponseBase(SinhVien sinhVien);

}
