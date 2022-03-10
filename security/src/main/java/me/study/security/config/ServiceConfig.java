package me.study.security.config;

import org.springframework.stereotype.Component;

@Component
public class ServiceConfig {
    public String getJwtSigningKey() {
        return "34345fsdgsf5345";
    }
}
