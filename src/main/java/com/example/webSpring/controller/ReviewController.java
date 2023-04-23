package com.example.webSpring.controller;

import com.example.webSpring.entity.Review;
import com.example.webSpring.repository.ReviewRepository;
import com.example.webSpring.response.ReviewResponse;
import com.example.webSpring.service.ReviewService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/review")
@RequiredArgsConstructor
@CrossOrigin
public class ReviewController {
    private final ReviewService reviewService;
    private final ReviewRepository reviewRepository;

    @GetMapping("/get/{bookId}")
    public ResponseEntity<List<Review>> getReview(@PathVariable Long bookId){
        return ResponseEntity.ok(reviewService.getReview(bookId));
    }
    @PostMapping("/add/{bookId}")
    public ResponseEntity<ReviewResponse> addReview(@PathVariable Long bookId, @RequestBody Review review, HttpServletRequest request){
        return ResponseEntity.ok(reviewService.addReview( bookId, review, request));
    }
}
