package com.example.springbatchplayground.controllers;

import org.springframework.batch.core.Job;

import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.NoSuchJobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class HomeController {

  @Autowired
  @Qualifier("printHelloJob")
  Job printHelloJob;

  @Autowired
  @Qualifier("rabbitJob")
  Job rabbitJob;

  @Autowired JobLauncher jobLauncher;

  @Autowired
  JobOperator jobOperator;

  @GetMapping(path = "/home")
  public String home() {
    return "home";
  }

  @GetMapping(path = "hello")
  public String hello() {
    System.out.println("hit the /hello endpoint");

    try {
      jobOperator.startNextInstance("rabbit-job");
    } catch (Exception e) {
      e.printStackTrace();
    }

    System.out.println("done launching the rabbit job");
    return "hello";
  }


  @GetMapping(path = "stats")
  public String stats() throws Exception {

    System.out.println("Job names: ");
    jobOperator.getJobNames().stream().forEach(System.out::println);

    List<Long> jobInstancesIds =  jobOperator.getJobInstances("print-hello-job", 0, 3);

    List<String> jobSummaries = jobInstancesIds.stream().map(id -> {
      try {
        return jobOperator.getSummary(id);
      } catch (NoSuchJobExecutionException e) {
        e.printStackTrace();
        return null;
      }
    }).filter(x -> x != null).collect(Collectors.toList());
    jobSummaries.stream().forEach(
            instance -> {
              System.out.println("Summary:" + instance);
            }
    );

    return "stats";
  }
}
