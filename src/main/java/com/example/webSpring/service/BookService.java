package com.example.webSpring.service;

import com.example.webSpring.entity.Book;
import com.example.webSpring.repository.BookRepository;
import com.example.webSpring.upload.FileService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class BookService {
    private final BookRepository bookRepository;
    private final ObjectMapper objectMapper;
    private final FileService fileService;

    public Book save(@RequestParam("model") String jsonObject, @RequestParam("file") MultipartFile file) throws IOException {
        Book book = objectMapper.readValue(jsonObject, Book.class);
        String generatedFileName = fileService.storageFile(file);
        book.setUrlImage(generatedFileName);
        return bookRepository.save(book);
    }

}
