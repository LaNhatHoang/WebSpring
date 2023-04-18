package com.example.webSpring.repository;

import com.example.webSpring.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    @Query("select r from Review as r where r.book.id = :bookId")
    List<Review> findByBookId(Long bookId);
}
