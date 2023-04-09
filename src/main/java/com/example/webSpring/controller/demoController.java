package com.example.webSpring.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class demoController {
    @GetMapping("/users/all")
    public String home(){
        return "admin";
    }
    @GetMapping("/books/all")
    public String getbooks(){
        return "user";
    }
    @GetMapping("/")
    public String index(){
        return "Hello world";
    }
}
