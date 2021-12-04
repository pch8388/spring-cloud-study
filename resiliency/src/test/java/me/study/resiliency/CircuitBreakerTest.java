package me.study.resiliency;

import static org.assertj.core.api.Assertions.*;

import java.io.IOException;
import java.time.Duration;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class CircuitBreakerTest {

	private static final String CIRCUIT_BREAKER_NAME = "backend";
	private static MockWebServer server;
	private ResiliencyService resiliencyService;
	private CircuitBreakerRegistry registry;

	@BeforeAll
	static void setUpServer() throws IOException {
		server = new MockWebServer();
		server.start();
	}

	@AfterAll
	static void tearDownServer() throws IOException {
		server.shutdown();
	}

	@BeforeEach
	void initialize() {
		registry = CircuitBreakerRegistry.of(
			CircuitBreakerConfig.custom()
				.slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
				.slidingWindowSize(4)
				.slowCallDurationThreshold(Duration.ofSeconds(5))
				.slowCallRateThreshold(50)
				.waitDurationInOpenState(
					Duration.ofSeconds(3))  // open -> half-open 변경시간, open 된 후부터의 시간(추가로 호출해도 늘어나지 않음)
				.build());

		final CircuitBreaker circuitBreaker = registry.circuitBreaker(CIRCUIT_BREAKER_NAME);

		transitionToClosedState(CIRCUIT_BREAKER_NAME);

		final String url = String.format("http://localhost:%s", server.getPort());
		resiliencyService = new ResiliencyService(WebClient.create(), circuitBreaker, url);
	}

	@Test
	void success() {
		final String id = "100";
		final String expected = "welcome hello " + id;

		server.enqueue(
			new MockResponse()
				.addHeader("Content-Type", "application/json")
				.setResponseCode(200)
				.setBody("hello " + id)
		);

		final Mono<String> result = resiliencyService.welcome(id);

		StepVerifier.create(result)
			.expectNextMatches(expected::equals)
			.verifyComplete();

		checkHealthStatus(CIRCUIT_BREAKER_NAME, CircuitBreaker.State.CLOSED);

	}

	private void transitionToOpenState(String circuitBreakerName) {
		CircuitBreaker circuitBreaker = registry.circuitBreaker(circuitBreakerName);
		circuitBreaker.transitionToOpenState();;
	}

	private void transitionToClosedState(String circuitBreakerName) {
		CircuitBreaker circuitBreaker = registry.circuitBreaker(circuitBreakerName);
		circuitBreaker.transitionToClosedState();
	}

	private void checkHealthStatus(String circuitBreakerName, CircuitBreaker.State state) {
		CircuitBreaker circuitBreaker = registry.circuitBreaker(circuitBreakerName);
		assertThat(circuitBreaker.getState()).isEqualTo(state);
	}
}
