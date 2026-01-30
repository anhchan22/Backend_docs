package com.qlsv.dkmh.dto.response;
import java.time.LocalDate;
import java.util.Set;

import com.qlsv.dkmh.enums.Role;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse {
    String maSV;
    String ten;
    String matKhau;

    Set<String> roles;
}
