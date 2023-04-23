package com.example.webSpring.controller;

import com.example.webSpring.entity.Book;
import com.example.webSpring.repository.BookRepository;
import com.example.webSpring.response.BookResponse;
import com.example.webSpring.service.BookService;
import com.fasterxml.jackson.core.JsonProcessingException;
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
    public ResponseEntity<List<Book>> allBook(){
        List<Book> books = bookRepository.findAll();
        return ResponseEntity.ok(books);
    }
    @GetMapping("/getall")
    public ResponseEntity<List<Book>> getAllBook(){
        List<Book> books = bookRepository.findAll();
        return ResponseEntity.ok(books);
    }
    @GetMapping("/{bookId}")
    public ResponseEntity<Book> getId(@PathVariable Long bookId){
        return ResponseEntity.ok(bookRepository.findBookById(bookId));
    }
    @PostMapping("/add")
    public ResponseEntity<BookResponse> addBook(@RequestParam("model") String jsonObject, @RequestParam("file") MultipartFile file) throws IOException {
            return ResponseEntity.ok(bookService.addBook(jsonObject, file));
    }
    @PostMapping("/update/{id}")
    public ResponseEntity<BookResponse> updateBook(@PathVariable Long id,@RequestBody Book book){
        return ResponseEntity.ok(bookService.updateBook(id, book));
    }
    @PostMapping("/updateWithFile/{id}")
    public ResponseEntity<BookResponse> updateBookWithFile(@PathVariable Long id, @RequestParam("model") String jsonObject, @RequestParam("file") MultipartFile file) throws JsonProcessingException {
        return  ResponseEntity.ok(bookService.updateBookWithFile(id,jsonObject,file));
    }
    @PostMapping("/delete/{id}")
    public ResponseEntity<BookResponse> deleteBook(@PathVariable Long id){
        return ResponseEntity.ok(bookService.deleteBook(id));
    }
}
