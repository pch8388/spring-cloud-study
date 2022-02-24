package sc.ex.springbatch.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@RequiredArgsConstructor
@Slf4j
@Configuration
public class JobConfig {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job job() {
        return this.jobBuilderFactory.get("helloJob")
                .start(helloStepOne())
                .next(helloStepTwo())
                .build();
    }

    @Bean
    public Step helloStepOne() {
        return this.stepBuilderFactory.get("helloStepOne")
                .tasklet(((contribution, chunkContext) -> {

                    log.info("=====================");
                    log.info(">> Hello spring batch!!");
                    log.info("=====================");

                    // 한번만 실행되는 상태
                    return RepeatStatus.FINISHED;
                }))
                .build();
    }

    @Bean
    public Step helloStepTwo() {
        return this.stepBuilderFactory.get("helloStepOne")
                .tasklet(((contribution, chunkContext) -> {

                    log.info("=====================");
                    log.info(">> step2 was executed!");
                    log.info("=====================");

                    // 한번만 실행되는 상태
                    return RepeatStatus.FINISHED;
                }))
                .build();
    }
}
