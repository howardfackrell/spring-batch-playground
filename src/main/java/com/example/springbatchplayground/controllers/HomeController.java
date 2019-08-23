package com.example.springbatchplayground.controllers;

import batch.commands.Action;
import batch.commands.JobCommand;
import org.springframework.batch.core.*;

import org.springframework.batch.core.converter.DefaultJobParametersConverter;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.NoSuchJobExecutionException;
import org.springframework.batch.core.launch.NoSuchJobInstanceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

  @Autowired
  JobExplorer jobExplorer;

  Random random = new Random();

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

  @GetMapping(path = "printParams")
  public String printParams() throws Exception {

    JobParameters parameters =
        new JobParametersBuilder()
            .addLong("longParam", 42L, false)
            .addDouble("doubleParam", 3.14d, false)
            .addDate("dateParam", new Date(), false)
            .addString("stringParam", "Hello", false)
            .addString("uniqueParam", Instant.now().toString(), true)
            .toJobParameters();

    DefaultJobParametersConverter jobParametersConverter = new DefaultJobParametersConverter();
    Properties props = jobParametersConverter.getProperties(parameters);
    StringBuilder parametersString = new StringBuilder();
    props.forEach((k, v) -> parametersString.append(k).append("=").append(v).append("\n"));

    System.out.println("Parameters String: " + parametersString.toString());
    jobOperator.start("printParamJob", parametersString.toString());
    return "printParams";
  }


  @GetMapping(path = "stats")
  public String stats() throws Exception {

    System.out.println("Job names: ");
    jobOperator.getJobNames().stream().forEach(System.out::println);

    List<Long> jobInstancesIds =  jobOperator.getJobInstances("rabbit-job", 0, 3);

    List<Long> exectuationIds = jobOperator.getExecutions(jobInstancesIds.get(0));

    for (Long executionId : exectuationIds) {
      String summary = jobOperator.getSummary(executionId);
      System.out.println("Summary: " + summary);
    }

    return "stats";
  }

  @GetMapping(path = "jobs")
  public ModelAndView jobs() throws Exception {
    List<String> jobNames = jobExplorer.getJobNames();

    Map<String, Object> model = new HashMap<>();
    model.put("jobNames", jobNames);
    return new ModelAndView("jobs", model);
  }

  @GetMapping(path = "jobs/{jobName}/instances")
  public ModelAndView instances(@PathVariable("jobName") String jobName, @RequestParam("from") int from, @RequestParam("count") int count) throws Exception {

    List<JobInstance> instances = jobExplorer.getJobInstances(jobName, from, count);

    Map<Long, List<JobExecution>> instanceExecutions = new HashMap<>();

    for(JobInstance instance : instances) {
      instanceExecutions.put(instance.getInstanceId(), jobExplorer.getJobExecutions(instance));
    }

    Map<String, Object> model = new HashMap<>();

    model.put("jobName", jobName);
    model.put("instances", instances);
    model.put("instanceExecutions", instanceExecutions);

    return new ModelAndView("instances", model);
  }


}
