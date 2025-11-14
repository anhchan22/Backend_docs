package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
// danh dau class la 1 table
@Data
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //tu dong +1
    private Long id;
    private String username;
    private String password;
    private String email;
    private LocalDate dob;

}
