package com.qlsv.dkmh.mapper;

import com.qlsv.dkmh.dto.request.MonHocRequest;
import com.qlsv.dkmh.dto.response.MonHocResponse;
import com.qlsv.dkmh.entity.MonHoc;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-11-27T21:55:43+0700",
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

        String maMH = null;
        String tenMH = null;
        int soTinChi = 0;

        maMH = monHoc.getMaMH();
        tenMH = monHoc.getTenMH();
        soTinChi = monHoc.getSoTinChi();

        MonHocResponse monHocResponse = new MonHocResponse( maMH, tenMH, soTinChi );

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
