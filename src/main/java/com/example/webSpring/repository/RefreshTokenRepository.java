package com.example.webSpring.repository;

import com.example.webSpring.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    RefreshToken findByRefreshToken(String refreshToken);
    @Query(value = "select t from RefreshToken t where t.user.id = :id ")
    RefreshToken findRefreshTokenByUserId(Long id);
    @Modifying
    @Query(value = "delete from RefreshToken t where t.user.id = :id", nativeQuery = true)
    void deleteByUserId(Long id);
}
