package com.example.chunk_example_batch.content.basicBatch.batch.deciders;

import lombok.AllArgsConstructor;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class ReturnDecider implements JobExecutionDecider {
  Random random;
  public ReturnDecider(){
    this.random = new Random();
  }

  @Override
  public FlowExecutionStatus decide(JobExecution jobExecution, StepExecution stepExecution) {
    int probability = random.nextInt(0, 100);
    return new FlowExecutionStatus(probability < 30 ? "REFUND" : "THANKS");
  }
}
