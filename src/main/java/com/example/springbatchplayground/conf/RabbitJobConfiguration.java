package com.example.springbatchplayground.conf;

import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.JobParametersNotFoundException;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableBatchProcessing
public class RabbitJobConfiguration {

  @Autowired StepBuilderFactory stepBuilderFactory;

  @Autowired JobBuilderFactory jobBuilderFactory;

  @Autowired JobOperator jobOperator;


  @Bean
  @StepScope
  public Tasklet runningTasklet(@Value("#{jobParameters['id']}") Long id) {
      Tasklet tasklet = new Tasklet() {
          @Override
          public RepeatStatus execute(
                  StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
              System.out.println("...running... with id " + id);
              return RepeatStatus.FINISHED;
          }
      };
      return tasklet;
  }


  //    @Scope(value="step", proxyMode = ScopedProxyMode.TARGET_CLASS)
  @Bean
  public Step printRunningStep(Tasklet runningTasklet) {
//  public Step printRunningStep(@Value("#{jobParameters['id']}") Long id) {
    return stepBuilderFactory
        .get("rabbit-running-step")
        .tasklet(runningTasklet)
        .build();
  }

  @Bean
  public JobParametersIncrementer rabbitJobParametersIncrementer() {
    return new JobParametersIncrementer() {
      @Override
      public JobParameters getNext(JobParameters jobParameters) {
        Long id = jobParameters == null ? 0 : jobParameters.getLong("id");
        return new JobParametersBuilder().addLong("id", id + 1).toJobParameters();
      }
    };
  }

  @Bean
  public Job rabbitJob(Step printRunningStep) {
    return jobBuilderFactory
        .get("rabbit-job")
        .start(printRunningStep)
        .incrementer(rabbitJobParametersIncrementer())
        .build();
  }
}
