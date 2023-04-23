package com.example.webSpring.service;

import com.example.webSpring.entity.Book;
import com.example.webSpring.entity.Review;
import com.example.webSpring.entity.User;
import com.example.webSpring.repository.BookRepository;
import com.example.webSpring.repository.ReviewRepository;
import com.example.webSpring.repository.UserRepository;
import com.example.webSpring.response.ReviewResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final ReviewRepository reviewRepository;
    private final JwtService jwtService;

    public List<Review> getReview(Long bookId){
        return reviewRepository.findByBookId(bookId);
    }
    public ReviewResponse addReview(Long bookId, Review review, HttpServletRequest request){
        if(review.getStar()<=0 || review.getStar()>5){
            return ReviewResponse.builder().status(false).message("Số sao đánh giá không hợp lệ").build();
        }
        if(review.getComment().equals("")){
            return ReviewResponse.builder().status(false).message("Không để trống đánh giá").build();
        }
        String header = request.getHeader("Authorization");
        String token = header.substring(7);
        String email = jwtService.extractUsername(token);
        User user = userRepository.findByEmail(email);
        Book book = bookRepository.findBookById(bookId);
        if(user!=null && book!=null){
            Review r = Review.builder().user(user).book(book).comment(review.getComment()).star(review.getStar()).build();
            reviewRepository.save(r);
            return ReviewResponse.builder().status(true).message("Đánh giá thành công").build();
        }
        return ReviewResponse.builder().status(false).message("Bạn không thể thực hiện thao tác này").build();
    }
}
