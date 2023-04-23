package com.example.webSpring.controller;


import com.example.webSpring.entity.BuyBook;
import com.example.webSpring.response.BuyBookResponse;
import com.example.webSpring.service.BuyBookService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/order")
@RequiredArgsConstructor
@CrossOrigin
public class BuyBookController {
    private final BuyBookService buyBookService;

    @PostMapping("/add/{bookId}")
    public ResponseEntity<BuyBookResponse> add(HttpServletRequest request, @PathVariable Long bookId, @RequestBody BuyBook buyBook){
        return ResponseEntity.ok(buyBookService.add(request, bookId, buyBook));
    }
    @GetMapping("/get")
    public ResponseEntity<List<BuyBook>> get(HttpServletRequest request){
        return ResponseEntity.ok(buyBookService.get(request));
    }
    @PostMapping("/delete/{id}")
    public ResponseEntity<BuyBookResponse> delete(HttpServletRequest request,@PathVariable Long id){
        return ResponseEntity.ok(buyBookService.delete(request, id));
    }
}
