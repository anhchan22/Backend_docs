package com.qlsv.dkmh.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;


@Entity
@Data
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "users")
public class User {
    @Id
    String maSV;
    String matKhau;
    String ten;

    Set<String> roles;
}
