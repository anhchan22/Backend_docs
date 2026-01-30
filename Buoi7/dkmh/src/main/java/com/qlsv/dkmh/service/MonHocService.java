package com.qlsv.dkmh.service;

import com.qlsv.dkmh.dto.request.MonHocRequest;
import com.qlsv.dkmh.dto.response.MonHocResponse;
import com.qlsv.dkmh.entity.MonHoc;
import com.qlsv.dkmh.enums.ErrorCode;
import com.qlsv.dkmh.exception.AppException;
import com.qlsv.dkmh.mapper.MonHocMapper;
import com.qlsv.dkmh.repository.MonHocRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MonHocService {
    @Autowired
    MonHocRepository monHocRepo;

    @Autowired
    MonHocMapper monHocMapper;

    public List<MonHocResponse> getAllMonHocDTO() {
        return monHocRepo.findAllBy().stream()
                .map(monHocMapper::toResponse)
                .collect(Collectors.toList());
    }

    public List<MonHocResponse> getMonHocByHocKyDTO(String hocKy) {
        return monHocRepo.findMonHocByHocKy(hocKy).stream()
                .map(monHocMapper::toResponse)
                .collect(Collectors.toList());
    }

    public MonHocResponse createMonHoc(MonHocRequest request) {
        if(monHocRepo.existsById(request.getMaMH())) {
            throw new AppException(ErrorCode.MONHOC_EXISTS);
        }

        MonHoc monHoc = monHocMapper.toEntity(request);

        return monHocMapper.toResponse(monHocRepo.save(monHoc));
    }

}
