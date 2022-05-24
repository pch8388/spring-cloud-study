package com.example.firstservice;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class FirstService {

    private final BookRepository repository;
    private final OtherBookRepository otherBookRepository;

    public Book getBook(String id) {
        log.info("get book : {}", id);

        return checkRedisCache(id)
                .map(book -> {
                    log.info("redis cache hit");
                    return book;
                })
                .orElseGet(() -> {
                    log.info("redis cache miss");
                    return repository.save(otherBookRepository.getBook(id));
                });
    }

    private Optional<Book> checkRedisCache(String id) {
        return repository.findById(id);
    }
}