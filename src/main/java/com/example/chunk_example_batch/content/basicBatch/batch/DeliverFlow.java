package com.example.chunk_example_batch.content.basicBatch.batch;

import lombok.AllArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.support.SimpleFlow;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import web.darihuan.batch_examples.content.billing.batch.BillingBatchProcessing;
import web.darihuan.batch_examples.content.deliver.batch.deciders.DeliveryDecider;
import web.darihuan.batch_examples.content.deliver.batch.deciders.ReturnDecider;

import java.util.Map;

@Component
@AllArgsConstructor
public class DeliverFlow {
  private final StepBuilderFactory stepBuilderFactory;
  private final JobBuilderFactory jobBuilderFactory;

  private final DeliveryDecider deliveryDecider;

  private final ReturnDecider returnDecider;

  @Bean(name = "deliverFlowSimple")
  public SimpleFlow deliveryFlow(
      @Qualifier("package") Step packageStep,
      @Qualifier("address") Step addressStep,
      @Qualifier("customer") Step customerStep,
      @Qualifier("store") Step store,
      @Qualifier("leave") Step leave,
      @Qualifier("thanks") Step thanks,
      @Qualifier("refund") Step refund) {
    return new FlowBuilder<SimpleFlow>("deliveryFlow")
        .start(packageStep)
        .next(addressStep)
        .on("FAILED")
        .fail()
        .from(addressStep)
        .on("*")
        .to(deliveryDecider)
        .on("PRESENT")
        .to(customerStep)
        .from(deliveryDecider)
        .on("NOT PRESENT")
        .to(leave)
        .on("*")
        .to(returnDecider)
        .on("THANKS")
        .to(thanks)
        .from(returnDecider)
        .on("REFUND")
        .to(refund)
        .build();
  }

  @Bean(name = "package")
  public Step packageItemStep() {
    return this.stepBuilderFactory
        .get("packageItemStep")
        .tasklet(
            (stepContribution, chunkContext) -> {
              Map<String, Object> context = chunkContext.getStepContext().getJobParameters();
              System.out.printf(
                  "The item %s has been packaged on date %s!! \n",
                  context.get("item"), context.get("run.date").toString());
              return RepeatStatus.FINISHED;
            })
        .build();
  }

  @Bean(name = "address")
  public Step driveToAddressStep() {
    boolean GOT_LOST = false;
    return stepBuilderFactory
        .get("driverToAddressStep")
        .tasklet(
            (stepContribution, chunkContext) -> {
              Map<String, Object> context = chunkContext.getStepContext().getJobParameters();
              if (GOT_LOST) throw new RuntimeException("The Package has been lost");

              System.out.printf(
                  "successfully arrived at the address: %s \n", context.get("address"));
              return RepeatStatus.FINISHED;
            })
        .build();
  }

  @Bean(name = "customer")
  public Step givePackageTocustomerStep() {
    return stepBuilderFactory
        .get("givePackageTocustomerStep")
        .tasklet(
            (stepContribution, chunkContext) -> {
              Map<String, Object> context = chunkContext.getStepContext().getJobParameters();
              System.out.printf(
                  "Given the package to the customer: %s \n", context.get("customer"));

              return RepeatStatus.FINISHED;
            })
        .build();
  }

  @Bean("store")
  public Step storePackageStep() {
    return this.stepBuilderFactory
        .get("storePackageStep")
        .tasklet(
            (stepContribution, chunkContext) -> {
              System.out.println("Storing the package while the customer address is located");

              return RepeatStatus.FINISHED;
            })
        .build();
  }

  @Bean("leave")
  public Step leaveAtDoorStep() {
    return this.stepBuilderFactory
        .get("leaveAtDoorStep")
        .tasklet(
            (stepContribution, chunkContext) -> {
              System.out.println("leaving package at the door");

              return RepeatStatus.FINISHED;
            })
        .build();
  }

  @Bean("thanks")
  public Step thanksCustomerStep() {
    return this.stepBuilderFactory
        .get("thanksCustomerStep")
        .tasklet(
            (stepContribution, chunkContext) -> {
              Map<String, Object> contenxt = chunkContext.getStepContext().getJobParameters();
              System.out.printf("Thanks dear customer for buy %s  \n ", contenxt.get("item"));
              return RepeatStatus.FINISHED;
            })
        .build();
  }

  @Bean("refund")
  public Step giveRefundCustomerStep() {
    return this.stepBuilderFactory
        .get("giveRefundCustomerStep")
        .tasklet(
            (stepContribution, chunkContext) -> {
              System.out.printf("Sorry about the issues, your refound is proccessing");
              return RepeatStatus.FINISHED;
            })
        .build();
  }


}
