package me.study.resiliency.welcome;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@RestController
@Slf4j
public class ResiliencyController {

	private final ResiliencyService resiliencyService;

	@GetMapping("/welcome/{id}")
	public Mono<String> welcome(@PathVariable String id) {
		return resiliencyService.welcome(id);
	}
}
