package com.example.webSpring.service;

import com.example.webSpring.entity.Book;
import com.example.webSpring.entity.BuyBook;
import com.example.webSpring.entity.User;
import com.example.webSpring.repository.BookRepository;
import com.example.webSpring.repository.BuyBookRepository;
import com.example.webSpring.repository.UserRepository;
import com.example.webSpring.response.BuyBookResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BuyBookService {
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final BuyBookRepository buyBookRepository;
    private final JwtService jwtService;

    @Transactional
    public BuyBookResponse add(HttpServletRequest request, Long bookId, BuyBook buyBook){
        String header = request.getHeader("Authorization");
        String token = header.substring(7);
        String email = jwtService.extractUsername(token);
        User user = userRepository.findByEmail(email);
        Book book = bookRepository.findBookById(bookId);
        if(buyBook.getQuantity()>100000){
            return BuyBookResponse.builder().status(false).message("Không thể mua với số lượng nhiều hơn 100.000").build();
        }
        if(user!=null && book!=null && buyBook.getQuantity()>0){
            BuyBook or = BuyBook.builder().quantity(buyBook.getQuantity()).timeOrder(new Date(System.currentTimeMillis())).book(book).user(user).build();
            book.setSold(book.getSold()+ buyBook.getQuantity());
            buyBookRepository.save(or);
            bookRepository.save(book);
            return BuyBookResponse.builder().status(true).message("Đặt mua thành công").build();
        }
        return BuyBookResponse.builder().status(false).message("Bạn không thể thực hiện thao tác này").build();
    }
    public List<BuyBook> get(HttpServletRequest request){
        String header = request.getHeader("Authorization");
        String token = header.substring(7);
        String email = jwtService.extractUsername(token);
        return buyBookRepository.findByUserEmail(email);
    }
    @Transactional
    public BuyBookResponse delete(HttpServletRequest request, Long id){
        String header = request.getHeader("Authorization");
        String token = header.substring(7);
        String email = jwtService.extractUsername(token);
        BuyBook buyBook = buyBookRepository.findBuyBookById(id);
        if(buyBook.getUser().getEmail().equals(email)){
            Book book = buyBook.getBook();
            book.setSold(book.getSold()- buyBook.getQuantity());
            buyBookRepository.delete(buyBook);
            return BuyBookResponse.builder().status(true).message("Hủy mua thành công").build();
        }
        return  BuyBookResponse.builder().status(false).message("Bạn không thể thực hiện thao tác này").build();
    }
}
