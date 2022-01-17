package com.example.firstservice;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient("my-second-service")
public interface SecondClient {

    @GetMapping
    String home();
}
