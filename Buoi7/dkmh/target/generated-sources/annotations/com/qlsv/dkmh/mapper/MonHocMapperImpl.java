package com.qlsv.dkmh.mapper;

import com.qlsv.dkmh.dto.request.MonHocRequest;
import com.qlsv.dkmh.dto.response.MonHocResponse;
import com.qlsv.dkmh.entity.MonHoc;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-01-30T15:57:48+0700",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 23.0.1 (Oracle Corporation)"
)
@Component
public class MonHocMapperImpl implements MonHocMapper {

    @Override
    public MonHoc toEntity(MonHocRequest monHocRequest) {
        if ( monHocRequest == null ) {
            return null;
        }

        MonHoc monHoc = new MonHoc();

        monHoc.setMaMH( monHocRequest.getMaMH() );
        monHoc.setTenMH( monHocRequest.getTenMH() );
        monHoc.setSoTinChi( monHocRequest.getSoTinChi() );

        return monHoc;
    }

    @Override
    public MonHocResponse toResponse(MonHoc monHoc) {
        if ( monHoc == null ) {
            return null;
        }

        MonHocResponse monHocResponse = new MonHocResponse();

        monHocResponse.setMaMH( monHoc.getMaMH() );
        monHocResponse.setTenMH( monHoc.getTenMH() );
        monHocResponse.setSoTinChi( monHoc.getSoTinChi() );

        return monHocResponse;
    }

    @Override
    public void update(MonHoc monHoc, MonHocRequest monHocRequest) {
        if ( monHocRequest == null ) {
            return;
        }

        monHoc.setMaMH( monHocRequest.getMaMH() );
        monHoc.setTenMH( monHocRequest.getTenMH() );
        monHoc.setSoTinChi( monHocRequest.getSoTinChi() );
    }
}
