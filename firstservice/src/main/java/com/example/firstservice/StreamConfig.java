package com.example.firstservice;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;
import java.util.function.Supplier;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class StreamConfig {

    private final BookRepository repository;

    @Bean
    public Consumer<BookChangeModel> sink() {
        return changeModel -> {
            log.info("sink");
            if ("DELETE".equals(changeModel.getAction())) {
                repository.deleteById(changeModel.getId());
            }
        };
    }

    @Bean
    public Supplier<BookChangeModel> send() {
        return () -> {
            log.info("send");
            return new BookChangeModel("DELETE", "1");
        };
    }
}
