package com.example.webSpring.service;

import com.example.webSpring.entity.Book;
import com.example.webSpring.repository.BookRepository;
import com.example.webSpring.response.BookResponse;
import com.example.webSpring.response.FileResponse;
import com.example.webSpring.file.FileService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookService {
    private final BookRepository bookRepository;
    private final ObjectMapper objectMapper;
    private final FileService fileService;

    public BookResponse addBook(@RequestParam("model") String jsonObject, @RequestParam("file") MultipartFile file) throws IOException {
        Book book = objectMapper.readValue(jsonObject, Book.class);
        Book b = bookRepository.findByName(book.getName());
        if(b!=null && b.getAuthor().equals(book.getAuthor())){
            return BookResponse.builder().status(false).message("Book is exits").build();
        }
        FileResponse fileResponse = fileService.storageFile(file);
        if(fileResponse.isStatus()){
            book.setUrlImage(fileResponse.getNameImage());
            bookRepository.save(book);
            return BookResponse.builder().status(true).message("Add book success").build();
        }
        return BookResponse.builder().status(false).message(fileResponse.getMessage()).build();
    }
    public BookResponse deleteBook(Long id){
        Book book = bookRepository.findBookById(id);
        if(book==null){
            return BookResponse.builder().status(false).message("Book is not available").build();
        }
        bookRepository.delete(book);
        return BookResponse.builder().status(true).message("Delete book success").build();
    }
}
