package com.example.webSpring.repository;

import com.example.webSpring.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    RefreshToken findByRefreshToken(String refreshToken);
    @Query(value = "select t from RefreshToken t where t.user.id = :id ")
    RefreshToken findRefreshTokenByUserId(Long id);
}
