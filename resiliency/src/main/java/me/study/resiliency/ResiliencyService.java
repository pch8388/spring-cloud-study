package me.study.resiliency;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Slf4j
@Service
public class ResiliencyService {

	private final WebClient client;
	private final CircuitBreaker circuitBreaker;

	@Value("${uri}")
	private final String uri;

	public Mono<String> welcome(String id) {
		// CircuitBreaker circuitBreaker = registry.circuitBreaker("backend");

		return client.get().uri(uri + "/hello/" + id)
			.exchangeToMono(res -> res.bodyToMono(String.class))
			.flatMap(r -> Mono.just("welcome " + r))
			.transformDeferred(CircuitBreakerOperator.of(circuitBreaker))
			.onErrorResume(this::fallback);
	}

	private Mono<String> fallback(Throwable e) {
		log.error("Fallback : {}", e.getMessage());
		return Mono.just("fallback !!!");
	}
}
