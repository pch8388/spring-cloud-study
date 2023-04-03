package com.example.firstservice;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@RequiredArgsConstructor
@Slf4j
@Component
public class MessageConsumer {

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
}
