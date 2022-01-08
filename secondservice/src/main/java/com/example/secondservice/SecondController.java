package com.example.secondservice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RefreshScope
public class SecondController {

    @Value("${common.property}")
    private String property;

    @GetMapping
    public String home() {
        return "my property : " + property;
    }

    @GetMapping("/welcome")
    public String welcome() {
        return "Welcome to the Second service";
    }
}
