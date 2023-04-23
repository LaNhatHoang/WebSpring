package com.example.webSpring.service;

import com.example.webSpring.entity.Book;
import com.example.webSpring.repository.BookRepository;
import com.example.webSpring.response.BookResponse;
import com.example.webSpring.response.FileResponse;
import com.example.webSpring.file.FileService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
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
        if(b!=null && b.getName().equals(book.getName()) && b.getAuthor().equals(book.getAuthor())) {
            return BookResponse.builder().status(false).message("Sách đã tồn tại").build();
        }
        FileResponse fileResponse = fileService.storageFile(file);
        if(fileResponse.isStatus()){
            book.setUrlImage(fileResponse.getNameImage());
            book.setSold(0L);
            bookRepository.save(book);
            return BookResponse.builder().status(true).message("Thêm sách thành công").build();
        }
        return BookResponse.builder().status(false).message(fileResponse.getMessage()).build();
    }

    public BookResponse updateBook(Long id, Book book){
        Book b = bookRepository.findBookById(id);
        b.setName(book.getName());
        b.setAuthor(book.getAuthor());
        b.setAbout(book.getAbout());
        b.setReleaseDate(book.getReleaseDate());
        b.setNumberPage(book.getNumberPage());
        b.setCategory(book.getCategory());
        bookRepository.save(b);
        return BookResponse.builder().status(true).message("Sửa thành công").build();
    }
    public BookResponse updateBookWithFile(Long id, @RequestParam("model") String jsonObject, @RequestParam("file") MultipartFile file) throws JsonProcessingException {
        Book book = objectMapper.readValue(jsonObject, Book.class);
        Book b = bookRepository.findBookById(id);
        b.setName(book.getName());
        b.setAuthor(book.getAuthor());
        b.setAbout(book.getAbout());
        b.setReleaseDate(book.getReleaseDate());
        b.setNumberPage(book.getNumberPage());
        b.setCategory(book.getCategory());
        FileResponse fileResponse = fileService.storageFile(file);
        if(fileResponse.isStatus()){
            b.setUrlImage(fileResponse.getNameImage());
            bookRepository.save(b);
            return BookResponse.builder().status(true).message("Sửa thành công").build();
        }
        return BookResponse.builder().status(false).message(fileResponse.getMessage()).build();
    }

    public BookResponse deleteBook(Long id){
        bookRepository.deleteById(id);
        return BookResponse.builder().status(true).message("Xóa thành công").build();
    }
}
