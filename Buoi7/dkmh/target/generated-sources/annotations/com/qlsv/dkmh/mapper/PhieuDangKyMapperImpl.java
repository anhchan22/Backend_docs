package com.qlsv.dkmh.mapper;

import com.qlsv.dkmh.dto.request.DangKyRequest;
import com.qlsv.dkmh.dto.response.PhieuDangKyResponse;
import com.qlsv.dkmh.entity.LopHocPhan;
import com.qlsv.dkmh.entity.MonHoc;
import com.qlsv.dkmh.entity.PhieuDangKy;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-11-27T21:55:43+0700",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 23.0.1 (Oracle Corporation)"
)
@Component
public class PhieuDangKyMapperImpl implements PhieuDangKyMapper {

    @Override
    public PhieuDangKyResponse.RegisteredCourseDTO toRegisteredCourseDTO(PhieuDangKy phieuDangKy) {
        if ( phieuDangKy == null ) {
            return null;
        }

        PhieuDangKyResponse.RegisteredCourseDTO registeredCourseDTO = new PhieuDangKyResponse.RegisteredCourseDTO();

        registeredCourseDTO.setMaMH( phieuDangKyLopHocPhanMonHocMaMH( phieuDangKy ) );
        registeredCourseDTO.setTenMH( phieuDangKyLopHocPhanMonHocTenMH( phieuDangKy ) );
        registeredCourseDTO.setSoTinChi( phieuDangKyLopHocPhanMonHocSoTinChi( phieuDangKy ) );
        registeredCourseDTO.setGioHoc( phieuDangKyLopHocPhanKhungGioHoc( phieuDangKy ) );
        registeredCourseDTO.setGiangVien( phieuDangKyLopHocPhanGiangVien( phieuDangKy ) );
        registeredCourseDTO.setTenLop( phieuDangKyLopHocPhanTenLop( phieuDangKy ) );
        registeredCourseDTO.setPhongHoc( phieuDangKyLopHocPhanPhongHoc( phieuDangKy ) );

        return registeredCourseDTO;
    }

    @Override
    public List<PhieuDangKyResponse.RegisteredCourseDTO> toRegisteredCourseDTOList(List<PhieuDangKy> phieuDangKyList) {
        if ( phieuDangKyList == null ) {
            return null;
        }

        List<PhieuDangKyResponse.RegisteredCourseDTO> list = new ArrayList<PhieuDangKyResponse.RegisteredCourseDTO>( phieuDangKyList.size() );
        for ( PhieuDangKy phieuDangKy : phieuDangKyList ) {
            list.add( toRegisteredCourseDTO( phieuDangKy ) );
        }

        return list;
    }

    @Override
    public PhieuDangKy toEntity(DangKyRequest request) {
        if ( request == null ) {
            return null;
        }

        PhieuDangKy phieuDangKy = new PhieuDangKy();

        phieuDangKy.setHocKy( request.getHocKy() );

        return phieuDangKy;
    }

    @Override
    public void update(PhieuDangKy entity, DangKyRequest request) {
        if ( request == null ) {
            return;
        }

        entity.setHocKy( request.getHocKy() );
    }

    private String phieuDangKyLopHocPhanMonHocMaMH(PhieuDangKy phieuDangKy) {
        if ( phieuDangKy == null ) {
            return null;
        }
        LopHocPhan lopHocPhan = phieuDangKy.getLopHocPhan();
        if ( lopHocPhan == null ) {
            return null;
        }
        MonHoc monHoc = lopHocPhan.getMonHoc();
        if ( monHoc == null ) {
            return null;
        }
        String maMH = monHoc.getMaMH();
        if ( maMH == null ) {
            return null;
        }
        return maMH;
    }

    private String phieuDangKyLopHocPhanMonHocTenMH(PhieuDangKy phieuDangKy) {
        if ( phieuDangKy == null ) {
            return null;
        }
        LopHocPhan lopHocPhan = phieuDangKy.getLopHocPhan();
        if ( lopHocPhan == null ) {
            return null;
        }
        MonHoc monHoc = lopHocPhan.getMonHoc();
        if ( monHoc == null ) {
            return null;
        }
        String tenMH = monHoc.getTenMH();
        if ( tenMH == null ) {
            return null;
        }
        return tenMH;
    }

    private int phieuDangKyLopHocPhanMonHocSoTinChi(PhieuDangKy phieuDangKy) {
        if ( phieuDangKy == null ) {
            return 0;
        }
        LopHocPhan lopHocPhan = phieuDangKy.getLopHocPhan();
        if ( lopHocPhan == null ) {
            return 0;
        }
        MonHoc monHoc = lopHocPhan.getMonHoc();
        if ( monHoc == null ) {
            return 0;
        }
        int soTinChi = monHoc.getSoTinChi();
        return soTinChi;
    }

    private String phieuDangKyLopHocPhanKhungGioHoc(PhieuDangKy phieuDangKy) {
        if ( phieuDangKy == null ) {
            return null;
        }
        LopHocPhan lopHocPhan = phieuDangKy.getLopHocPhan();
        if ( lopHocPhan == null ) {
            return null;
        }
        String khungGioHoc = lopHocPhan.getKhungGioHoc();
        if ( khungGioHoc == null ) {
            return null;
        }
        return khungGioHoc;
    }

    private String phieuDangKyLopHocPhanGiangVien(PhieuDangKy phieuDangKy) {
        if ( phieuDangKy == null ) {
            return null;
        }
        LopHocPhan lopHocPhan = phieuDangKy.getLopHocPhan();
        if ( lopHocPhan == null ) {
            return null;
        }
        String giangVien = lopHocPhan.getGiangVien();
        if ( giangVien == null ) {
            return null;
        }
        return giangVien;
    }

    private String phieuDangKyLopHocPhanTenLop(PhieuDangKy phieuDangKy) {
        if ( phieuDangKy == null ) {
            return null;
        }
        LopHocPhan lopHocPhan = phieuDangKy.getLopHocPhan();
        if ( lopHocPhan == null ) {
            return null;
        }
        String tenLop = lopHocPhan.getTenLop();
        if ( tenLop == null ) {
            return null;
        }
        return tenLop;
    }

    private String phieuDangKyLopHocPhanPhongHoc(PhieuDangKy phieuDangKy) {
        if ( phieuDangKy == null ) {
            return null;
        }
        LopHocPhan lopHocPhan = phieuDangKy.getLopHocPhan();
        if ( lopHocPhan == null ) {
            return null;
        }
        String phongHoc = lopHocPhan.getPhongHoc();
        if ( phongHoc == null ) {
            return null;
        }
        return phongHoc;
    }
}
