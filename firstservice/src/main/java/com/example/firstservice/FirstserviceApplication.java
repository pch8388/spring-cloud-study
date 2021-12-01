package com.example.firstservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@RestController
@Slf4j
public class FirstserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(FirstserviceApplication.class, args);
	}

	@GetMapping("/welcome")
	public String welcome() {
		return "Welcome to the First service";
	}

	@GetMapping("/hello/{id}")
	public String hello(@PathVariable String id) throws InterruptedException {
		log.info("First service welcome start !!");
		Thread.sleep(3000);
		return "hello " + id;
	}
}
