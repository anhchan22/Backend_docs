package com.qlsv.dkmh.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.qlsv.dkmh.enums.Role;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.HashSet;
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

//    @ElementCollection(fetch = FetchType.EAGER)
//    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "ma_sv"))
//    @Enumerated(EnumType.STRING)
//    @Column(name = "role")
//    @Builder.Default
//    Set<Role> roles = new HashSet<>();
    Set<String> roles;
}
