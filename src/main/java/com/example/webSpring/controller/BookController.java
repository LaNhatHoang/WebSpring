package com.example.webSpring.controller;

import com.example.webSpring.entity.Book;
import com.example.webSpring.repository.BookRepository;
import com.example.webSpring.response.BookResponse;
import com.example.webSpring.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/book")
@RequiredArgsConstructor
@CrossOrigin
public class BookController {
    private final BookService bookService;
    private  final BookRepository bookRepository;
    @GetMapping("/all")
    public ResponseEntity<List<Book>> getAllBook(){
        List<Book> books = bookRepository.findAll();
        return ResponseEntity.ok(books);
    }
    @PostMapping("/add")
    public ResponseEntity<BookResponse> addBook(@RequestParam("model") String jsonObject, @RequestParam("file") MultipartFile file) throws IOException {
            return ResponseEntity.ok(bookService.addBook(jsonObject, file));
    }
    @PostMapping("/delete/{id}")
    public ResponseEntity<BookResponse> deleteBook(@PathVariable Long id){
        return ResponseEntity.ok(bookService.deleteBook(id));
    }
}
