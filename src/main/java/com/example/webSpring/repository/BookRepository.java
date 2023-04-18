package com.example.webSpring.repository;

import com.example.webSpring.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {
    Book findByName(String name);
    Book findBookById(Long id);

    @Override
    void deleteById(Long aLong);
}
