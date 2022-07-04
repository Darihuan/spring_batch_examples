package com.example.chunk_example_batch.content.basicBatch.batch.deciders;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
@Component
public class DeliveryDecider implements JobExecutionDecider {
  @Override
  public FlowExecutionStatus decide(JobExecution jobExecution, StepExecution stepExecution) {
    String result = LocalTime.now().getHour() < 12 ? "PRESENT" : "NOT PRESENT";
    System.out.printf("Decider Result is %s \n", result);
    return new FlowExecutionStatus(result);
  }
}
