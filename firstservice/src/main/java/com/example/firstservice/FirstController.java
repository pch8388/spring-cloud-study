package com.example.firstservice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RefreshScope
public class FirstController {

    @Value("${my.property}")
    private String property;

    @GetMapping
    public String home() {
        return "my property : " + property;
    }

    @GetMapping("/welcome")
    public String welcome() {
        return "Welcome to the First service";
    }

    @GetMapping("/hello/{id}")
    public String hello(@PathVariable String id) throws InterruptedException {
        log.info("First service welcome start !!");
        Thread.sleep(5000);
        return "hello " + id;
    }
}
