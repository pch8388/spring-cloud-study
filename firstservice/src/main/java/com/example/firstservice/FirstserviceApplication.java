package com.example.firstservice;

import com.example.firstservice.config.ServiceConfig;
import feign.Logger;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootApplication
@EnableFeignClients
@EnableEurekaClient
@RequiredArgsConstructor
public class FirstserviceApplication {

	private final ServiceConfig config;

	public static void main(String[] args) {
		SpringApplication.run(FirstserviceApplication.class, args);
	}

	@Bean
	public Logger.Level feignLoggerLevel() {
		return Logger.Level.FULL;
	}

	@Bean
	public JedisConnectionFactory jedisConnectionFactory() {
		RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
		configuration.setHostName(config.getRedisServer());
		configuration.setPort(config.getPort());
		return new JedisConnectionFactory(configuration);
	}

	@Bean
	public RedisTemplate<String, Object> redisTemplate() {
		RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
		redisTemplate.setConnectionFactory(jedisConnectionFactory());
		return redisTemplate;
	}
}
