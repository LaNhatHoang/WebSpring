package com.example.webSpring.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String author;
    private String about ;
    private Date releaseDate;
    private int numberPage;
    private String category;
    private int sold;
    private String urlImage;

    @OneToMany(mappedBy = "book")
    @JsonIgnore
    private List<Review> reviews;
}
