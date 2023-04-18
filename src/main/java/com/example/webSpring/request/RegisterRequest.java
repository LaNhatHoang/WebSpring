package com.example.webSpring.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class RegisterRequest {
    private String firstname;
    private String lastname;
    private String email;
    private String password;
}
