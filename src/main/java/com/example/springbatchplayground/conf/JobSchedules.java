package com.example.springbatchplayground.conf;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;

@Configuration
@EnableScheduling
@Profile("batch")
public class JobSchedules {

    @Autowired
    JobLauncher jobLauncher;

    @Autowired
    JobOperator jobOperator;

    @Autowired
    Job rabbitJob;

    @Scheduled(zone = "MST", cron="0/10 * * * * *")
    public void runTheRabbit() {

        try {
            List<Long> instanceIds = jobOperator.getJobInstances("rabbit-job", 0, 1);
            Long id = instanceIds.get(0);

            JobParameters parameters = new JobParametersBuilder().addLong("id", id).toJobParameters();

//            jobOperator.startNextInstance("rabbit-job");
            jobLauncher.run(rabbitJob, rabbitJob.getJobParametersIncrementer().getNext(parameters));
        } catch (Exception e) {
            System.err.println("couldn't start the rabbit job");
            e.printStackTrace();
        }
    }
}
