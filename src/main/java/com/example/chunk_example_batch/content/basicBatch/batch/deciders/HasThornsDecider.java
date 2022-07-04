package com.example.chunk_example_batch.content.basicBatch.batch.deciders;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class HasThornsDecider implements JobExecutionDecider {
  @Override
  public FlowExecutionStatus decide(JobExecution jobExecution, StepExecution stepExecution) {
    String CLEAN = new Random().nextInt(0,10)<5?"CLEAN":"NOT_CLEAN";
    System.out.println("The FLower is " + CLEAN);
    return new FlowExecutionStatus(CLEAN);
  }
}
