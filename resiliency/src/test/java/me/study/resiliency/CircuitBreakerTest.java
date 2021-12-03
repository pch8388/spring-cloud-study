package me.study.resiliency;

import java.io.IOException;
import java.time.Duration;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import okhttp3.mockwebserver.MockWebServer;

public class CircuitBreakerTest extends AbstractCircuitBreakerTest {

	private static MockWebServer server;

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
		final CircuitBreakerRegistry registry = CircuitBreakerRegistry.of(
			CircuitBreakerConfig.custom()
				.slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
				.slidingWindowSize(4)
				.slowCallDurationThreshold(Duration.ofSeconds(5))
				.slowCallRateThreshold(50)
				.waitDurationInOpenState(
					Duration.ofSeconds(3))  // open -> half-open 변경시간, open 된 후부터의 시간(추가로 호출해도 늘어나지 않음)
				.build());

		final String url = String.format("http://localhost:%s", server.getPort());
	}

	@Test
	void fail_test() {
	}
}
