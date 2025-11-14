package com.example.demo.dto.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UserUpdateRequest {
    private String password;
    private String email;
    private LocalDate dob;
}
