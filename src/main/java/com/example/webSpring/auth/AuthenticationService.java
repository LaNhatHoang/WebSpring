package com.example.webSpring.auth;



import com.example.webSpring.entity.Token;
import com.example.webSpring.entity.User;
import com.example.webSpring.repository.TokenRepository;
import com.example.webSpring.repository.UserRepository;
import com.example.webSpring.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final int MAX_FAILED_LOGIN = 5;
    private final long TIME_LOCK = 5 * 60 * 1000;
    private static final int EXPIED_TIME = 1 * 60 * 60 * 1000;

    public AuthenticationResponse register(RegisterRequest request) {
        User theUser = userRepository.findByEmail(request.getEmail());
        if (theUser != null){
            return AuthenticationResponse
                    .builder().status(false)
                    .message("A user with " +request.getEmail() +" already exists")
                    .build();
        }
        var user = User.builder()
                .firstName(request.getFirstname())
                .lastName(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role("USER")
                .failedLogin(0)
                .locked(false)
                .timeLock(new Date(System.currentTimeMillis()))
                .build();
        var savedUser = userRepository.save(user);
        var jwtToken = jwtService.generateToken(user);
        saveUserToken(savedUser, jwtToken);
        return AuthenticationResponse.builder()
                .status(true)
                .message("Register success")
                .token(jwtToken)
                .role(user.getRole())
                .build();
    }
    public AuthenticationResponse authenticate(AuthenticationRequest request){
        User user = userRepository.findByEmail(request.getEmail());
        if(user == null){
            return AuthenticationResponse
                    .builder().status(false)
                    .message("Username not found").build();
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
        String jwtToken = jwtService.generateToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, jwtToken);
        return AuthenticationResponse.builder()
                .status(true)
                .message("Login success")
                .token(jwtToken)
                .role(user.getRole())
                .build();
    }
    private void revokeAllUserTokens(User user) {
        Token validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if (validUserTokens == null)
            return;
        tokenRepository.delete(validUserTokens);
    }
    private void saveUserToken(User user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .timeExpire(new Date(System.currentTimeMillis() + EXPIED_TIME))
                .build();
        tokenRepository.save(token);
    }
    private String parseLockTime(long time){
        long minute = time/60000;
        long second = (time % 60000)/1000;
        return Long.toString(minute) + " phút " + Long.toString(second) + " giây";
    }
}
