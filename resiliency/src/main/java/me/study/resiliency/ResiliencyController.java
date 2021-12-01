package me.study.resiliency;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@RestController
@Slf4j
public class ResiliencyController {

	private final WebClient client;

	@GetMapping("/welcome/{id}")
	public Mono<String> welcome(@PathVariable String id) {
		CircuitBreaker circuitBreaker = CircuitBreaker.ofDefaults("backendA");

		return client.get().uri("localhost:8081/hello/" + id)
			.exchangeToMono(res -> res.bodyToMono(String.class))
			.flatMap(r -> Mono.just("welcome " + r))
			.transformDeferred(CircuitBreakerOperator.of(circuitBreaker))
			.onErrorResume(error -> Mono.just("onError"));
	}
}
