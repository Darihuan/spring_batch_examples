package com.example.chunk_example_batch.content.basicBatch.batch;

import lombok.AllArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.job.flow.support.SimpleFlow;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;


//@Component
@AllArgsConstructor
public class DeliverBatchProcessing {

  private final JobBuilderFactory jobBuilderFactory;



  @Bean("deliver")
  public Job deliverPackageJob(
          @Qualifier("deliverFlow")SimpleFlow deliverFlow, @Qualifier("package")Step packageStep) {
    return this.jobBuilderFactory
        .get("deliverPackageJob")
            .start(packageStep)
            .on("*")
            .to(deliverFlow)
            .end()
            .build();
  }



}
