package com.qlsv.dkmh.mapper;

import com.qlsv.dkmh.dto.request.DangKyRequest;
import com.qlsv.dkmh.dto.response.PhieuDangKyResponse;
import com.qlsv.dkmh.entity.PhieuDangKy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

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

    // Map từ DangKyRequest sang PhieuDangKy entity
    // LƯU Ý: sinhVien và lopHocPhan PHẢI set thủ công trong service vì:
    // 1. Request chỉ có String (maSV, maLop) nhưng Entity cần Object đầy đủ
    // 2. MapStruct KHÔNG THỂ tự động query database để fetch entity
    // 3. Cần validation và exception handling (sinh viên có tồn tại? lớp đã đầy?)
    //
    // Ví dụ trong service:
    //   PhieuDangKy phieu = mapper.toEntity(request);
    //   phieu.setSinhVien(sinhVienRepo.findById(request.getMaSV()).get());
    //   phieu.setLopHocPhan(lopHocPhanRepo.findById(maLop).get());
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "sinhVien", ignore = true)
    @Mapping(target = "lopHocPhan", ignore = true)
    PhieuDangKy toEntity(DangKyRequest request);


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "sinhVien", ignore = true)
    @Mapping(target = "lopHocPhan", ignore = true)
    void update(@MappingTarget PhieuDangKy entity, DangKyRequest request);
}
