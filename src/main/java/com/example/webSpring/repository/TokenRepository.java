package com.example.webSpring.repository;

import com.example.webSpring.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TokenRepository extends JpaRepository<Token, Long>{
    Token findByToken(String token);
    @Query(value = """
      select t from Token t inner join User u\s
      on t.user.id = u.id\s
      where u.id = :id \s
      """)
    Token findAllValidTokenByUser(Long id);
}
