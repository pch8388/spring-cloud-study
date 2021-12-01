package me.study.resiliency.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import reactor.netty.http.client.HttpClient;

@Configuration
public class WebClientConfig {

	@Bean
	WebClient webClient() {
		return WebClient.builder()
			.clientConnector(
				new ReactorClientHttpConnector(
					HttpClient.create().option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10_000)
						.doOnConnected(connection -> connection.addHandlerLast(new ReadTimeoutHandler(30))
																.addHandlerLast(new WriteTimeoutHandler(30)))))
			.build();
	}
}
