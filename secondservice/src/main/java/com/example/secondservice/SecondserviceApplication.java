package com.example.secondservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class SecondserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(SecondserviceApplication.class, args);
	}

	@GetMapping("/welcome")
	public String welcome() {
		return "Welcome to the Second service";
	}

}