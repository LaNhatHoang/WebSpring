package com.example.webSpring.service;

import com.example.webSpring.entity.Book;
import com.example.webSpring.entity.Review;
import com.example.webSpring.entity.User;
import com.example.webSpring.repository.BookRepository;
import com.example.webSpring.repository.ReviewRepository;
import com.example.webSpring.repository.UserRepository;
import com.example.webSpring.response.ReviewResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final ReviewRepository reviewRepository;

    public List<Review> getReview(Long bookId){
        return reviewRepository.findByBookId(bookId);
    }
    public ReviewResponse addReview(Long userId, Long bookId, Review review){
        if(review.getComment().equals("")){
            return ReviewResponse.builder().status(false).message("Không để trống đánh giá").build();
        }
        User user = userRepository.findUserById(userId);
        Book book = bookRepository.findBookById(bookId);
        if(user!=null && book!=null){
            Review r = Review.builder().user(user).book(book).comment(review.getComment()).star(review.getStar()).build();
            reviewRepository.save(r);
            return ReviewResponse.builder().status(true).message("Đánh giá thành công").build();
        }
        return ReviewResponse.builder().status(false).message("Có lỗi xảy ra trong khi đánh giá").build();
    }
}
