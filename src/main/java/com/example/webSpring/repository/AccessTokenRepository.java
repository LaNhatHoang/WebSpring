package com.example.webSpring.repository;

import com.example.webSpring.entity.AccessToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AccessTokenRepository extends JpaRepository<AccessToken, Long>{
    AccessToken findByAccessToken(String accessToken);
    @Query(value = "select t from AccessToken t where t.user.id = :id ")
    AccessToken findAccessTokenByUserId(Long id);
    @Modifying
    @Query(value = "delete from AccessToken t where t.user.id = :id", nativeQuery = true)
    void deleteByUserId(Long id);
}
