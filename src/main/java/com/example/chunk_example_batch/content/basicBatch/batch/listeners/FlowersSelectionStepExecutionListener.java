package com.example.chunk_example_batch.content.basicBatch.batch.listeners;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class FlowersSelectionStepExecutionListener implements StepExecutionListener {
  @Override
  public void beforeStep(StepExecution stepExecution) {
    System.out.println("Executing before step logic");
  }

  @Override
  public ExitStatus afterStep(StepExecution stepExecution) {
    System.out.println("Executing after step logic");
    String flowerType = stepExecution.getJobParameters().getString("type");
    return !Objects.requireNonNull(flowerType).equalsIgnoreCase("roses") ?
            new ExitStatus("TRIM_REQUIRED") :
            new ExitStatus("NO_TRIM_REQUIRED");
  }
}
