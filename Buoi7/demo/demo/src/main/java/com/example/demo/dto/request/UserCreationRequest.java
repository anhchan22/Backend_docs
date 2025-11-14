package com.example.demo.dto.request;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Data
public class UserCreationRequest {
    private String username;
    private String password;
    private String email;
    private LocalDate dob;
}
