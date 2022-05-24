package com.example.firstservice;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class OtherBookRepository {
    public Book getBook(String id) {
        return new Book(id, "book-name-" + id, "author-" + id, new Random().nextInt(100000));
    }
}
