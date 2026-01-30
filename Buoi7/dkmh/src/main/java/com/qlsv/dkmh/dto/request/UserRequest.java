package com.qlsv.dkmh.dto.request;

import com.qlsv.dkmh.enums.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserRequest {
    String ten;
    @NotBlank(message = "USERNAME_INVALID")
    String maSV;

    @Size(min = 8, message = "PASSWORD_INVALID")
    String matKhau;

    String diachi;
    String khoa;
    String ngaySinh;
    String queQuan;
}
