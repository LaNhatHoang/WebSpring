package com.example.webSpring.auth;

import com.example.webSpring.entity.AccessToken;
import com.example.webSpring.entity.RefreshToken;
import com.example.webSpring.entity.User;
import com.example.webSpring.repository.AccessTokenRepository;
import com.example.webSpring.repository.RefreshTokenRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authenticationService.register(request));
    }
    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request, HttpServletResponse response) {
        return ResponseEntity.ok(authenticationService.login(request, response));
    }
    @PostMapping("/logout")
    public void logout(HttpServletRequest request, HttpServletResponse response){
        authenticationService.logout(request,response);
    }
    @PostMapping("/refresh")
    public ResponseEntity<AuthenticationResponse> refresh( HttpServletRequest request, HttpServletResponse response){
        return ResponseEntity.ok(authenticationService.refreshToken(request,response));
    }
}
