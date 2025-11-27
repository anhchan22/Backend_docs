package com.qlsv.dkmh.service;

import com.qlsv.dkmh.dto.request.LopHocPhanRequest;
import com.qlsv.dkmh.dto.response.LopHocPhanResponse;
import com.qlsv.dkmh.entity.LopHocPhan;
import com.qlsv.dkmh.entity.MonHoc;
import com.qlsv.dkmh.enums.ErrorCode;
import com.qlsv.dkmh.exception.AppException;
import com.qlsv.dkmh.mapper.LopHocPhanMapper;
import com.qlsv.dkmh.repository.LopHocPhanRepository;
import com.qlsv.dkmh.repository.MonHocRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LopHocPhanService {
    @Autowired
    private LopHocPhanRepository lopHocPhanRepository;

    @Autowired
    private MonHocRepository monHocRepository;
    @Autowired
    private LopHocPhanMapper lopHocPhanMapper;

    public List<LopHocPhanResponse> getLopHocPhanByMonHoc(String maMH, String hocKy) {
        return lopHocPhanRepository.findByMonHocAndHocKy(maMH, hocKy)
                .stream()
                .map(lopHocPhanMapper::toResponse)
                .collect(Collectors.toList());
    }

    public LopHocPhanResponse createLopHocPhan(LopHocPhanRequest request) {
        // Tìm môn học theo mã
        MonHoc monHoc = monHocRepository.findById(request.getMaMH())
                .orElseThrow(() -> new AppException(ErrorCode.MONHOC_NOT_FOUND));

        // Kiểm tra xem mã lớp đã tồn tại chưa
        if (lopHocPhanRepository.existsById(request.getMaLop())) {
            throw new AppException(ErrorCode.MALOP_EXISTS);
        }

        LopHocPhan lopHocPhan = lopHocPhanMapper.toEntity(request);
        return lopHocPhanMapper.toResponse(lopHocPhanRepository.save(lopHocPhan));
    }
}
