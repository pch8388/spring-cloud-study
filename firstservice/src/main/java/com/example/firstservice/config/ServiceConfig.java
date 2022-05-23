package com.example.firstservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ServiceConfig {

    @Value("${redis.server}")
    private String redisServer;

    @Value("${redis.port}")
    private String port;

    public String getRedisServer() {
        return redisServer;
    }

    public Integer getPort() {
        return Integer.valueOf(port);
    }
}
