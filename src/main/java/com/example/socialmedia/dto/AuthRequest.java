package com.example.socialmedia.dto;

import lombok.Data;

@Data
public class AuthRequest {
    private String email;
    private String password;
}
