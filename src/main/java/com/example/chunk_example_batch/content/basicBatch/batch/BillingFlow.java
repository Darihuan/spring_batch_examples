package com.example.chunk_example_batch.content.basicBatch.batch;

import lombok.AllArgsConstructor;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.support.SimpleFlow;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class BillingFlow {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean("billingFlowSimple")
    public SimpleFlow billingFlow() {
        BillingBatchProcessing sendBilling;
        sendBilling = new BillingBatchProcessing(this.jobBuilderFactory, this.stepBuilderFactory);
        return new FlowBuilder<SimpleFlow>("billingFlow")
                .start(sendBilling.sendInvoiceStep())
                .build();


    }

//    @Bean("billingJobStep")
//    public Step nestedBillingJobStep() {
//        BillingBatchProcessing sendBilling =
//                new BillingBatchProcessing(this.jobBuilderFactory, this.stepBuilderFactory);
//
//        return this.stepBuilderFactory
//                .get("nestedBillingjobStep")
//                .job(sendBilling.billingJob(sendBilling.sendInvoiceStep()))
//                .build();
//    }
}
