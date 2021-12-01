package me.study.resiliency;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StopWatch;
import org.springframework.web.client.RestTemplate;

public class LoadTest {

	private static final Logger log = LoggerFactory.getLogger(LoadTest.class);
	private static final AtomicInteger counter = new AtomicInteger(0);

	@DisplayName("한번에 thread 100개를 만들어 동시에 호출하는 테스트")
	@Test
	void load() throws BrokenBarrierException, InterruptedException {
		ExecutorService es = Executors.newFixedThreadPool(100);
		CyclicBarrier barrier = new CyclicBarrier(101);

		RestTemplate rt = new RestTemplate();

		for (int i = 0; i < 100; i++) {
			es.submit(() -> {
				final int idx = counter.addAndGet(1);

				barrier.await();

				log.info("Thread {}", idx);

				StopWatch sw = new StopWatch();
				sw.start();

				String res = rt.getForObject("http://localhost:8080/welcome/{idx}", String.class, idx);

				sw.stop();
				log.info("Elapsed : {} {} / {}", idx, sw.getTotalTimeSeconds(), res);

				return null;
			});
		}

		barrier.await();
		StopWatch main = new StopWatch();
		main.start();

		es.shutdown();
		es.awaitTermination(100, TimeUnit.SECONDS);

		main.stop();
		log.info("Total : {}", main.getTotalTimeSeconds());
	}
}
