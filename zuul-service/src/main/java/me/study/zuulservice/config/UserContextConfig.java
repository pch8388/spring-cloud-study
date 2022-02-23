package me.study.zuulservice.config;

import me.study.zuulservice.utils.UserContextInterceptor;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@Configuration
public class UserContextConfig {

    @LoadBalanced
    @Bean
    public RestTemplate restTemplate() {
        RestTemplate template = new RestTemplate();
        List<ClientHttpRequestInterceptor> interceptors = template.getInterceptors();

        Optional.ofNullable(interceptors)
                .ifPresentOrElse(clientHttpRequestInterceptors -> {
                    clientHttpRequestInterceptors.add(new UserContextInterceptor());
                    template.setInterceptors(clientHttpRequestInterceptors);
                }, () -> template.setInterceptors(List.of(new UserContextInterceptor())));

        return template;
    }
}
