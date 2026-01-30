package com.qlsv.dkmh.mapper;

import com.qlsv.dkmh.dto.response.DraftResponse;
import com.qlsv.dkmh.entity.LopHocPhan;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DraftMapper {

    @Mapping(source = "maLop", target = "maLop")
    @Mapping(source = "tenLop", target = "tenLop")
    @Mapping(source = "monHoc.maMH", target = "maMH")
    @Mapping(source = "monHoc.tenMH", target = "tenMH")
    @Mapping(source = "monHoc.soTinChi", target = "soTinChi")
    @Mapping(source = "khungGioHoc", target = "khungGioHoc")
    @Mapping(source = "giangVien", target = "giangVien")
    @Mapping(source = "phongHoc", target = "phongHoc")
    DraftResponse.LopHocPhanTrongDraft toLopHocPhanTrongDraft(LopHocPhan lopHocPhan);

    List<DraftResponse.LopHocPhanTrongDraft> toLopHocPhanTrongDraftList(List<LopHocPhan> lopHocPhanList);
}
