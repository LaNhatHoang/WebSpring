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
    private Long sold;
    private String urlImage;

    @OneToMany(mappedBy = "book",cascade = CascadeType.REMOVE)
    @JsonIgnore
    private Set<Review> reviews;

    @OneToMany(mappedBy = "book", cascade = CascadeType.REMOVE)
    @JsonIgnore
    private List<BuyBook> buyBooks;
}
