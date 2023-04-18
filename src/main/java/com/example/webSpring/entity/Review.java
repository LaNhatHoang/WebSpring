package com.example.webSpring.entity;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String comment;
    private int star;

    @ManyToOne
    @JoinColumn()
    @JsonIncludeProperties(value = {"email"})
    private User user;

    @ManyToOne
    @JoinColumn()
    @JsonIgnore
    private Book book;

}
