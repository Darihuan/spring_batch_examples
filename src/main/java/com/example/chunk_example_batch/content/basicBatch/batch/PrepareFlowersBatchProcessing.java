package com.example.chunk_example_batch.content.basicBatch.batch;

import lombok.AllArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.flow.support.SimpleFlow;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.stereotype.Component;
import web.darihuan.batch_examples.content.prepareFlowers.batch.deciders.HasThornsDecider;
import web.darihuan.batch_examples.content.prepareFlowers.batch.listeners.FlowersSelectionStepExecutionListener;

@Component
@AllArgsConstructor
public class PrepareFlowersBatchProcessing {
  private final JobBuilderFactory jobBuilderFactory;
  private final StepBuilderFactory stepBuilderFactory;
  private final HasThornsDecider hasThornsDecider;

  private FlowersSelectionStepExecutionListener flowersSelectionStepExecutionListener;

  //  @Bean
  //  public Job prepareFlowers() {
  //    return this.jobBuilderFactory
  //        .get("prepareFlowersJob")
  //        .start(selectFlowersStep())
  //        .next(hasThornsDecider)
  //        .on("NOT_CLEAN")
  //        .to(removeThornsStep())
  //        .next(arrangeFlowersStep())
  //        .from(hasThornsDecider)
  //        .on("*")
  //        .to(arrangeFlowersStep())
  //        .end()
  //        .build();
  //  }

  @Bean(name = "flowers")
  public Job prepareFlowers(@Qualifier("select")Step selectStep,
                            @Qualifier("deliverFlowSimple")SimpleFlow deliverFlow,
                            @Qualifier("billingFlowSimple")SimpleFlow billingFlow,
                            @Qualifier("arrange")Step arrangleStep,
                            @Qualifier("remove")Step removeThorns) {
    return this.jobBuilderFactory
            .get("prepareFlowersJob")
            .start(selectStep)
            .on("TRIM_REQUIRED")
            .to(removeThorns)
            .next(arrangleStep)
            .split(new SimpleAsyncTaskExecutor())
            .add(deliverFlow,billingFlow)
            .from(selectStep)
            .on("*")
            .to(arrangleStep)
            .split(new SimpleAsyncTaskExecutor())
            .add(deliverFlow,billingFlow)
            .end()
            .build();
  }

  @Bean("select")
  public Step selectFlowersStep() {
    return this.stepBuilderFactory
        .get("selectFlowersStep")
        .tasklet(
            (stepContribution, chunkContext) -> {
              System.out.println("Selecting the more beautiful flowers.");
              return RepeatStatus.FINISHED;
            })
        .listener(flowersSelectionStepExecutionListener)
        .build();
  }

  @Bean("remove")
  public Step removeThornsStep() {
    return this.stepBuilderFactory
        .get("removeThornsStep")
        .tasklet(
            (stepContribution, chunkContext) -> {
              System.out.println("Removing away all the thorns");
              return RepeatStatus.FINISHED;
            })
        .build();
  }

  @Bean("arrange")
  public Step arrangeFlowersStep() {
    return this.stepBuilderFactory
        .get("arrangeFlowersStep")
        .tasklet(
            (stepContribution, chunkContext) -> {
              System.out.println("Creating a GREAT arrange");
              return RepeatStatus.FINISHED;
            })
        .build();
  }
}
