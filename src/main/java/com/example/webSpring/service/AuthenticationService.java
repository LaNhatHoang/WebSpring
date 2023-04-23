package com.example.webSpring.service;


import com.example.webSpring.entity.AccessToken;
import com.example.webSpring.entity.RefreshToken;
import com.example.webSpring.entity.User;
import com.example.webSpring.repository.AccessTokenRepository;
import com.example.webSpring.repository.RefreshTokenRepository;
import com.example.webSpring.repository.UserRepository;
import com.example.webSpring.request.AuthenticationRequest;
import com.example.webSpring.request.RegisterRequest;
import com.example.webSpring.response.AuthenticationResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final AccessTokenRepository accessTokenRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final int MAX_FAILED_LOGIN = 5;
    private final long TIME_LOCK = 5 * 60 * 1000;
    private static final Long EXPIRED_ACCESS_TIME = 1 * 60 * 60 * 1000L;
    private static final Long EXPIRED_REFRESH_TIME = 6 * 2592000000L;
    private static final int MAX_AGE_COOKIE = 6 * 30 * 24 * 60 * 60;

    public AuthenticationResponse register(RegisterRequest request) {
        User theUser = userRepository.findByEmail(request.getEmail());
        if (theUser != null){
            return AuthenticationResponse
                    .builder().status(false)
                    .message("Tài khoản " +request.getEmail() +" đã được đăng ký")
                    .build();
        }
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role("USER")
                .failedLogin(0)
                .locked(false)
                .timeLock(new Date(System.currentTimeMillis()))
                .build();
        userRepository.save(user);
        return AuthenticationResponse.builder()
                .status(true)
                .message("Đăng ký thành công")
                .build();
    }
    public AuthenticationResponse login(AuthenticationRequest request, HttpServletResponse response){
        User user = userRepository.findByEmail(request.getEmail());
        if(user == null){
            return AuthenticationResponse
                    .builder().status(false)
                    .message("Tài khoản không tồn tại").build();
        }
        if( !passwordEncoder.matches(request.getPassword(), user.getPassword())){
            if(!user.isLocked()){
                int failedLogin = user.getFailedLogin();
                if(failedLogin < MAX_FAILED_LOGIN - 1){
                    user.setFailedLogin(failedLogin + 1);
                    userRepository.save(user);
                    return AuthenticationResponse
                            .builder().status(false)
                            .message("Bạn đã đăng nhập sai " + (failedLogin +1 ) + " lần. Nếu sai quá 5 lần tài khoản của bạn sẽ bị khóa").build();
                }
                else{
                    user.setLocked(true);
                    user.setFailedLogin(5);
                    user.setTimeLock(new Date( System.currentTimeMillis() + TIME_LOCK));
                    userRepository.save(user);
                    return AuthenticationResponse
                            .builder().status(false)
                            .message("Bạn đã đăng nhập sai quá 5" + " lần. Tài khoản của bạn khóa trong " + (TIME_LOCK/60000) +" phút").build();
                }
            }
            long timeLock = user.getTimeLock().getTime() - System.currentTimeMillis();
            if (timeLock >= 0){
                return AuthenticationResponse
                        .builder().status(false)
                        .message("Tài khoản của bạn đang bị khóa hãy thử lại sau " + parseLockTime(timeLock) )
                        .build();
            }
            else {
                user.setLocked(false);
                user.setTimeLock(new Date(System.currentTimeMillis()));
                user.setFailedLogin(1);
                userRepository.save(user);
                return AuthenticationResponse
                        .builder().status(false)
                        .message("Bạn đã đăng nhập sai " + 1 + " lần. Nếu sai quá 5 lần tài khoản của bạn sẽ bị khóa")
                        .build();
            }
        }
        long timeLock = user.getTimeLock().getTime() - System.currentTimeMillis();
        if(timeLock >= 0){
            return AuthenticationResponse
                    .builder()
                    .status(false)
                    .message("Tài khoản của bạn đang bị khóa hãy thử lại sau " + parseLockTime(timeLock) )
                    .build();
        }
        else {
            user.setLocked(false);
            user.setTimeLock(new Date(System.currentTimeMillis()));
            user.setFailedLogin(0);
            userRepository.save(user);
        }
        Authentication authentication =  authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        revokeAccessTokens(user);
        revokeRefreshTokens(user);
        saveRefreshToken(user,refreshToken);
        saveAccessToken(user, accessToken);
        Cookie cookie = new Cookie("rft", refreshToken);
        cookie.setMaxAge(MAX_AGE_COOKIE);
        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
        return AuthenticationResponse.builder()
                .status(true)
                .message("Đăng nhập thành công")
                .id(user.getId())
                .accessToken(accessToken)
//                .refreshToken(refreshToken)
                .role(user.getRole())
                .build();
    }
    public void logout(HttpServletRequest request, HttpServletResponse response){
        final String authHeader = request.getHeader("Authorization");
        final String acToken;
        if (authHeader == null ||!authHeader.startsWith("Bearer ")) {
            return;
        }
        acToken = authHeader.substring(7);
        AccessToken accessToken = accessTokenRepository.findByAccessToken(acToken);
        if (accessToken != null) {
            User user = accessToken.getUser();
            RefreshToken refreshToken = refreshTokenRepository.findRefreshTokenByUserId(user.getId());
            accessTokenRepository.delete(accessToken);
            refreshTokenRepository.delete(refreshToken);
            Cookie cookie = new Cookie("rft", null);
            cookie.setMaxAge(0);
            response.addCookie(cookie);
            SecurityContextHolder.clearContext();
        }
    }
    public AuthenticationResponse refreshToken(HttpServletRequest request, HttpServletResponse response){
        String token = "";
        Cookie[] cookies = request.getCookies();
        for(Cookie c:cookies){
            if(c.getName().equals("rft")){
                token = c.getValue();
            }
        }
        RefreshToken refreshToken = refreshTokenRepository.findByRefreshToken(token);
        if(refreshToken == null || refreshToken.getTimeExpire().getTime() < System.currentTimeMillis()){
            return  AuthenticationResponse.builder()
                    .status(false).message("Không thể Refresh Token").build();
        }
        User user = refreshToken.getUser();
        String acToken = jwtService.generateAccessToken(user);
        String rfToken = jwtService.generateRefreshToken(user);
        revokeAccessTokens(user);
        revokeRefreshTokens(user);
        saveAccessToken(user, acToken);
        saveRefreshToken(user,rfToken);
        Cookie cookie = new Cookie("rft", rfToken);
        cookie.setMaxAge(MAX_AGE_COOKIE);
        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
        return AuthenticationResponse.builder()
                .status(true)
                .accessToken(acToken)
//                .refreshToken(rfToken)
                .build();
    }
    @Transactional
    private void revokeAccessTokens(User user) {
        AccessToken accessToken = accessTokenRepository.findAccessTokenByUserId(user.getId());
        if (accessToken == null)
            return;
        accessTokenRepository.delete(accessToken);
    }
    @Transactional
    private void revokeRefreshTokens(User user) {
        RefreshToken refreshToken = refreshTokenRepository.findRefreshTokenByUserId(user.getId());
        if (refreshToken == null)
            return;
        refreshTokenRepository.delete(refreshToken);
    }
    private void saveAccessToken(User user, String accessToken) {
        AccessToken token = AccessToken.builder()
                .user(user)
                .accessToken(accessToken)
                .timeExpire(new Date(System.currentTimeMillis() + EXPIRED_ACCESS_TIME))
                .build();
        accessTokenRepository.save(token);
    }
    private void saveRefreshToken(User user, String refreshToken) {
        RefreshToken token = RefreshToken.builder()
                .user(user)
                .refreshToken(refreshToken)
                .timeExpire(new Date(System.currentTimeMillis() + EXPIRED_REFRESH_TIME))
                .build();
        refreshTokenRepository.save(token);
    }
    private String parseLockTime(long time){
        long minute = time/60000;
        long second = (time % 60000)/1000;
        return Long.toString(minute) + " phút " + Long.toString(second) + " giây";
    }
}
