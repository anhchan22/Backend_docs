package com.qlsv.dkmh.mapper;

import com.qlsv.dkmh.dto.request.LopHocPhanRequest;
import com.qlsv.dkmh.dto.response.LopHocPhanResponse;
import com.qlsv.dkmh.entity.LopHocPhan;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-11-27T21:55:43+0700",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 23.0.1 (Oracle Corporation)"
)
@Component
public class LopHocPhanMapperImpl implements LopHocPhanMapper {

    @Override
    public LopHocPhan toEntity(LopHocPhanRequest request) {
        if ( request == null ) {
            return null;
        }

        LopHocPhan lopHocPhan = new LopHocPhan();

        lopHocPhan.setMaLop( request.getMaLop() );
        lopHocPhan.setTenLop( request.getTenLop() );
        lopHocPhan.setSoSvToiDa( request.getSoSvToiDa() );
        lopHocPhan.setPhongHoc( request.getPhongHoc() );
        lopHocPhan.setKhungGioHoc( request.getKhungGioHoc() );
        lopHocPhan.setHocKy( request.getHocKy() );
        lopHocPhan.setGiangVien( request.getGiangVien() );

        return lopHocPhan;
    }

    @Override
    public LopHocPhanResponse toResponse(LopHocPhan entity) {
        if ( entity == null ) {
            return null;
        }

        String maLop = null;
        String tenLop = null;
        int soSvToiDa = 0;
        int soSvHienTai = 0;
        String khungGioHoc = null;
        String phongHoc = null;
        String giangVien = null;

        maLop = entity.getMaLop();
        tenLop = entity.getTenLop();
        soSvToiDa = entity.getSoSvToiDa();
        soSvHienTai = entity.getSoSvHienTai();
        khungGioHoc = entity.getKhungGioHoc();
        phongHoc = entity.getPhongHoc();
        giangVien = entity.getGiangVien();

        LopHocPhanResponse lopHocPhanResponse = new LopHocPhanResponse( maLop, tenLop, soSvToiDa, soSvHienTai, khungGioHoc, phongHoc, giangVien );

        return lopHocPhanResponse;
    }

    @Override
    public void update(LopHocPhan entity, LopHocPhanRequest request) {
        if ( request == null ) {
            return;
        }

        entity.setMaLop( request.getMaLop() );
        entity.setTenLop( request.getTenLop() );
        entity.setSoSvToiDa( request.getSoSvToiDa() );
        entity.setPhongHoc( request.getPhongHoc() );
        entity.setKhungGioHoc( request.getKhungGioHoc() );
        entity.setHocKy( request.getHocKy() );
        entity.setGiangVien( request.getGiangVien() );
    }
}
