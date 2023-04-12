package com.example.webSpring.repository;

import com.example.webSpring.entity.AccessToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AccessTokenRepository extends JpaRepository<AccessToken, Long>{
    AccessToken findByAccessToken(String accessToken);
//    @Query(value = """
//      select t from Token t inner join User u\s
//      on t.user.id = u.id\s
//      where u.id = :id \s
//      """)
    @Query(value = "select t from AccessToken t where t.user.id = :id ")
    AccessToken findAccessTokenByUserId(Long id);
}
