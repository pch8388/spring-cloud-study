package com.example.firstservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class FirstserviceApplication {
	public static void main(String[] args) {
		SpringApplication.run(FirstserviceApplication.class, args);
	}
}
