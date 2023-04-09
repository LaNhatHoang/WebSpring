package com.example.webSpring.controller;

import com.example.webSpring.entity.Book;
import com.example.webSpring.repository.BookRepository;
import com.example.webSpring.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    public ResponseEntity<String> addBook(@RequestParam("model") String jsonObject, @RequestParam("file") MultipartFile file){
        try{
            bookService.save(jsonObject, file);
            return ResponseEntity.ok("add book success");
        }catch (Exception e){
            return ResponseEntity.ok("upload failed");
        }
    }
}
