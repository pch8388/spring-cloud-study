package me.study.resiliency.config;

import java.time.Duration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;

@Configuration
public class CustomCircuitBreakerConfig {

	@Bean
	public CircuitBreakerRegistry circuitBreakerRegistry() {
		return CircuitBreakerRegistry.of(
			CircuitBreakerConfig.custom()
				.slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
				.slidingWindowSize(4)
				.slowCallDurationThreshold(Duration.ofSeconds(5))
				.slowCallRateThreshold(50)
				.waitDurationInOpenState(Duration.ofSeconds(3))  // open -> half-open 변경시간, open 된 후부터의 시간(추가로 호출해도 늘어나지 않음)
				.build());
	}

	@Bean
	public CircuitBreaker circuitBreaker(CircuitBreakerRegistry circuitBreakerRegistry) {
		return circuitBreakerRegistry.circuitBreaker("backend");
	}
}
