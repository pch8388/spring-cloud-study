package me.study.resiliency;

import static org.assertj.core.api.Assertions.*;

import java.io.IOException;
import java.time.Duration;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import me.study.resiliency.welcome.ResiliencyService;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class CircuitBreakerTest {

	private static final String CIRCUIT_BREAKER_NAME = "backend";
	public static final int SLIDING_WINDOW_SIZE = 2;
	private static MockWebServer server;
	private ResiliencyService resiliencyService;
	private CircuitBreaker circuitBreaker;

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
		CircuitBreakerRegistry registry = CircuitBreakerRegistry.of(
			CircuitBreakerConfig.custom()
				.slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
				.slidingWindowSize(SLIDING_WINDOW_SIZE)
				.slowCallDurationThreshold(Duration.ofSeconds(5))
				.slowCallRateThreshold(50)
				.waitDurationInOpenState(
					Duration.ofSeconds(3))  // open -> half-open 변경시간, open 된 후부터의 시간(추가로 호출해도 늘어나지 않음)
				.build());

		circuitBreaker = registry.circuitBreaker(CIRCUIT_BREAKER_NAME);

		transitionToClosedState();

		resiliencyService = new ResiliencyService(WebClient.create(), circuitBreaker, "http://localhost", String.valueOf(server.getPort()));
	}

	@DisplayName("요청에 성공하면 서킷브레이커는 closed 상태이다")
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

		checkHealthStatus(CircuitBreaker.State.CLOSED);

	}

	@DisplayName("요청에 sliding window size 횟수만큼 실패하면 서킷브레이커는 open 상태이다")
	@Test
	void circuitBreaker_open_500() {
		final String id = "100";

		for (int i = 0; i < SLIDING_WINDOW_SIZE; i++) {
			server.enqueue(
				new MockResponse()
					.setResponseCode(500)
			);
		}

		for (int i = 0; i < SLIDING_WINDOW_SIZE; i++) {
			StepVerifier.create(resiliencyService.welcome(id))
				.expectNext("fallback !!!")
				.verifyComplete();
		}

		checkHealthStatus(CircuitBreaker.State.OPEN);
	}

	@DisplayName("요청에 sliding window size 횟수보다 작게 실패하면 서킷브레이커는 closed 상태이다")
	@Test
	void fall_short_sliding_window_size() {
		final int FALL_SHORT_SIZE = SLIDING_WINDOW_SIZE - 1;
		final String id = "100";

		for (int i = 0; i < FALL_SHORT_SIZE; i++) {
			server.enqueue(
				new MockResponse()
					.setResponseCode(500)
			);
		}

		for (int i = 0; i < FALL_SHORT_SIZE; i++) {
			StepVerifier.create(resiliencyService.welcome(id))
				.expectNext("fallback !!!")
				.verifyComplete();
		}

		checkHealthStatus(CircuitBreaker.State.CLOSED);
	}

	private void transitionToOpenState() {
		circuitBreaker.transitionToOpenState();
	}

	private void transitionToClosedState() {
		circuitBreaker.transitionToClosedState();
	}

	private void checkHealthStatus(CircuitBreaker.State state) {
		assertThat(circuitBreaker.getState()).isEqualTo(state);
	}
}
