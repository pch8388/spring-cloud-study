package com.example.secondservice;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RefreshScope
@RequiredArgsConstructor
public class SecondController {

    private final StreamBridge streamBridge;

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

    @GetMapping("/send")
    public String send() {
        streamBridge.send("send-out-0", new BookChangeModel("DELETE", "1"));
        return "success";
    }
}
