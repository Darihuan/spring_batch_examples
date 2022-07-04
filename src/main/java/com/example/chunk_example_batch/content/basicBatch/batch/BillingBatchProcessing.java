package com.example.chunk_example_batch.content.basicBatch.batch;

import lombok.AllArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;

import java.util.Map;


@AllArgsConstructor
public class BillingBatchProcessing {
  private final JobBuilderFactory jobBuilderFactory;
  private final StepBuilderFactory stepBuilderFactory;

  @Bean("billingJob")
  public Job billingJob(@Qualifier("sendInvoiceStep") Step sendInvoiceStep) {
    return this.jobBuilderFactory.get("billingsJob").start(sendInvoiceStep).build();
  }

  @Bean(name = "sendInvoiceStep")
  public Step sendInvoiceStep() {
    return this.stepBuilderFactory
        .get("sendInvoiceStep")
        .tasklet(
            (stepContribution, chunkContext) -> {
              Map<String, Object> context = chunkContext.getStepContext().getJobParameters();
              System.out.printf(
                  "Sending invoice to customer %s to the address: %s \n",
                  context.get("customer"), context.get("address"));
              return RepeatStatus.FINISHED;
            })
        .build();
  }
}
