package me.study.resiliency;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;

@AutoConfigureWebTestClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
	classes = ResiliencyApplication.class)
public abstract class AbstractCircuitBreakerTest {

	protected static final String BACKEND = "backend";

	@Autowired
	protected CircuitBreakerRegistry circuitBreakerRegistry;

	@BeforeEach
	void setUp() {
		transitionToClosedState(BACKEND);
	}

	protected void transitionToOpenState(String circuitBreakerName) {
		CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker(circuitBreakerName);
		circuitBreaker.transitionToOpenState();;
	}

	protected void transitionToClosedState(String circuitBreakerName) {
		CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker(circuitBreakerName);
		circuitBreaker.transitionToClosedState();
	}

	protected void checkHealthStatus(String circuitBreakerName, CircuitBreaker.State state) {
		CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker(circuitBreakerName);
		assertThat(circuitBreaker.getState()).isEqualTo(state);
	}
}
