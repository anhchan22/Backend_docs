package com.qlsv.dkmh.mapper;

import com.qlsv.dkmh.dto.request.MonHocRequest;
import com.qlsv.dkmh.dto.response.MonHocResponse;
import com.qlsv.dkmh.entity.MonHoc;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface MonHocMapper {
    MonHoc toEntity(MonHocRequest monHocRequest);
    MonHocResponse toResponse(MonHoc monHoc);
    void update (@MappingTarget MonHoc monHoc, MonHocRequest monHocRequest);
}
