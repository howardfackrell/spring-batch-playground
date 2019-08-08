package com.example.springbatchplayground.conf;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.support.JobRegistryBeanPostProcessor;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobOperator;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableBatchProcessing
public class JobConfiguration {

    @Autowired
    JobBuilderFactory jobBuilderFactory;

    @Autowired
    StepBuilderFactory stepBuilderFactory;

    @Autowired
    JobRepository jobRepository;

    @Bean
    public Step printHelloStep() {
        Step printHelloStep = stepBuilderFactory.get("print-hello-step")
                .tasklet(new Tasklet() {
                    @Override
                    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
                        Thread.sleep(2000);
                        System.out.println("hello world");
                        return RepeatStatus.FINISHED;
                    }
                })
                .allowStartIfComplete(true)
                .build();

        return printHelloStep;
    }

    @Bean
    public Job printHelloJob(Step printHelloStep) {
        Job job = jobBuilderFactory.get("print-hello-job")
                .start(printHelloStep)
                .build();
        return job;
    }


    @Bean
    public SimpleJobOperator jobOperator(JobExplorer jobExplorer,
                                         JobRepository jobRepository,
                                         JobRegistry jobRegistry,
                                         JobLauncher jobLauncher) {

        SimpleJobOperator jobOperator = new SimpleJobOperator();

        jobOperator.setJobExplorer(jobExplorer);
        jobOperator.setJobRepository(jobRepository);
        jobOperator.setJobRegistry(jobRegistry);
        jobOperator.setJobLauncher(jobLauncher);

        return jobOperator;
    }

    @Bean
    public JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor(JobRegistry jobRegistry) {
        JobRegistryBeanPostProcessor postProcessor = new JobRegistryBeanPostProcessor();
        postProcessor.setJobRegistry(jobRegistry);
        return postProcessor;
    }

//    @Bean
//    public StepScope stepScope() {
//        final StepScope stepScope = new StepScope();
//        stepScope.setAutoProxy(true);
//        return stepScope;
//    }

}


