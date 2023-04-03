package com.example.secondservice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

@Slf4j
@Component
public class MessageSupplier {

    @Bean
    public Supplier<BookChangeModel> send() {
        return () -> {
            log.info("send");
            return new BookChangeModel("DELETE", "1");
        };
    }
}
