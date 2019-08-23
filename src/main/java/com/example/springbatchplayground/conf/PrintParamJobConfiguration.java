package com.example.springbatchplayground.conf;

import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Configuration
public class PrintParamJobConfiguration {

    @Autowired
    JobBuilderFactory jobBuilderFactory;

    @Autowired
    StepBuilderFactory stepBuilderFactory;

    @Bean
    @JobScope
    public Step printParamStep(
            @Value("#{jobParameters['longParam']}") Long longParam,
            @Value("#{jobParameters['doubleParam']}") Double doubleParam,
            @Value("#{jobParameters['dateParam']}") Date dateParam,
            @Value("#{jobParameters['stringParam']}") String stringParam) {

        Step printParamStep = stepBuilderFactory.get("printParamStep")
                .tasklet(new Tasklet() {
                    @Override
                    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
                        System.out.println("Running printPararm Step: " + longParam + " " + doubleParam + " " + stringParam + " " + dateParam);
                        return RepeatStatus.FINISHED;
                    }
                })
                .build();

        return printParamStep;

    }

    public JobParameters parameters() {
    JobParameters parameters =
        new JobParametersBuilder()
            .addLong("longParam", 43L)
            .addDouble("doubleParam", 3.14d)
            .addDate("dateParam", new Date())
            .addString("stringParam", "Hello")
            .addString("uniqueString", Instant.now().toString())
            .toJobParameters();

        return parameters;
    }

    @Bean
    public Job printParamJob(Step printParamStep) {
        Job printParamJob = jobBuilderFactory
                .get("printParamJob")
                .start(printParamStep)
                .validator(new JobParametersValidator() {
                    @Override
                    public void validate(JobParameters jobParameters) throws JobParametersInvalidException {
                        List<String> errors = new ArrayList<>();
                        if (jobParameters.getLong("longParam") == null) {
                            errors.add("longParameter is missing");
                        }

                        if (jobParameters.getDouble("doubleParam") == null) {
                            errors.add("doubleParameter is missing");
                        }

                        if (jobParameters.getString("stringParam") == null) {
                            errors.add("stringParameter is missing");
                        }

                        if (jobParameters.getDate("dateParam") == null) {
                            errors.add("dateParameter is missing");
                        }

                        if (errors.size() > 0) {
                            String message = String.join(" ", errors);
                            throw new JobParametersInvalidException(message);
                        }
                    }
                })
                .build();

        return printParamJob;
    }
}
