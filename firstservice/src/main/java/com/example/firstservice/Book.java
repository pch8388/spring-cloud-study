package com.example.firstservice;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash("book")
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Book {
    @Id
    private String id;
    private String name;
    private String author;
    private int price;
}
