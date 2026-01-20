package com.qlsv.dkmh.enums;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.Map;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public enum ErrorCode {
    INVALID_KEY(1001, "Key invalid"),
    MONHOC_EXISTS(1002, "User existed"),
    INVALID_MALOP(1003, "Mã lớp học phần phải tối thiểu 6 ký tự"),
    SV_NOT_FOUND(1004, "Sinh viên không tồn tại"),
    LOP_NOT_FOUND(1005, "Lớp học phần không tồn tại"),
    MONHOC_NOT_FOUND(1006, "Môn học không tồn tại"),
    DKHP_NOT_FOUND(1007, "Đăng ký học phần không tồn tại"),
    EXCEED_MAX_CREDITS(1008, "Vượt quá số tín chỉ cho phép"),
    SCHEDULE_CONFLICT(1010, "Xung đột lịch học"),
    INVALID_MAMONHOC(1011, "Mã môn học phải tối thiểu 6 ký tự"),
    INVALID_SOSV_TOIDA(1012, "Lớp học đã đầy"),
    MALOP_EXISTS(1009, "Lớp học phần đã tồn tại"),
    USER_EXISTED(1013, "User existed"),
    UNAUTHENTICATED(1014, "Unauthenticated"),;

    final int code;
    final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
