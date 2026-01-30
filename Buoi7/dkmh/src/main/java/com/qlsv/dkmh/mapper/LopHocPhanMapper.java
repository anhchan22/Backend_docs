package com.qlsv.dkmh.mapper;

import com.qlsv.dkmh.dto.request.LopHocPhanRequest;
import com.qlsv.dkmh.dto.response.LopHocPhanResponse;
import com.qlsv.dkmh.entity.LopHocPhan;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface LopHocPhanMapper {
    LopHocPhan toEntity (LopHocPhanRequest request);
    LopHocPhanResponse toResponse (LopHocPhan entity);
    void update (@MappingTarget LopHocPhan entity, LopHocPhanRequest request);
}
