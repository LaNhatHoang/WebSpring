package com.example.webSpring.repository;

import com.example.webSpring.entity.BuyBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BuyBookRepository extends JpaRepository<BuyBook, Long> {

    BuyBook findBuyBookById(Long id);
    @Query("select b from BuyBook b where b.user.email = :email")
    List<BuyBook> findByUserEmail(String email);
}
