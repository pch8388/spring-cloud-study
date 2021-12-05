package me.study.resiliency.welcome;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class ResiliencyService {

	private final WebClient client;
	private final CircuitBreaker circuitBreaker;
	private final String uri;
	private final String port;

	public ResiliencyService(WebClient client, CircuitBreaker circuitBreaker,
		@Value("${api.uri:localhost}") String uri, @Value("${api.port:8081}") String port) {
		this.client = client;
		this.circuitBreaker = circuitBreaker;
		this.uri = uri;
		this.port = port;
	}

	public Mono<String> welcome(String id) {
		return client.mutate().build()
			.get().uri(uri + ":" + port + "/hello/" + id)
			.accept(MediaType.APPLICATION_JSON)
			.retrieve()
			.onStatus(HttpStatus::is4xxClientError, response -> Mono.error(new IllegalArgumentException("4xx")))
			.onStatus(HttpStatus::is5xxServerError, response -> Mono.error(new IllegalArgumentException("5xx")))
			.bodyToMono(String.class).log()
			.flatMap(r -> Mono.just("welcome " + r))
			.transformDeferred(CircuitBreakerOperator.of(circuitBreaker))
			.onErrorResume(this::fallback);
	}

	private Mono<String> fallback(Throwable e) {
		log.error("Fallback : {}", e.getMessage());
		return Mono.just("fallback !!!");
	}
}
