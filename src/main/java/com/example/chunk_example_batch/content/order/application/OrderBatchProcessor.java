package com.example.chunk_example_batch.content.order.application;

import com.example.chunk_example_batch.content.order.application.readers.OrderFieldSetMapper;
import com.example.chunk_example_batch.content.order.domain.Order;
import lombok.AllArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class OrderBatchProcessor {
  private final JobBuilderFactory jobBuilderFactory;
  private final StepBuilderFactory stepBuilderFactory;

  @Bean("order_job")
  private Job orderJob(@Qualifier("csv_step") Step csvStep) {
    return this.jobBuilderFactory.get("order_job").start(csvStep).build();
  }

  @Bean("csv_step")
  public Step csvChunkStep(@Qualifier("order_reader") ItemReader<Order> orderReader) {
    return this.stepBuilderFactory
        .get("CSV_step")
        .<Order, Order>chunk(30)
        .reader(orderReader)
        .writer(
            (list) -> {
              System.out.printf("Received a list of size %s \n", list.size());
              list.forEach(System.out::println);
            })
        .build();
  }

  @Bean("order_reader")
  public ItemReader<Order> itemReader() {

    FlatFileItemReader<Order> itemReader = new FlatFileItemReader<>();
    itemReader.setLinesToSkip(1);
    itemReader.setResource(new FileSystemResource("./data/shipped_orders.csv"));

    DefaultLineMapper<Order> lineMapper = new DefaultLineMapper<>();
    DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer(",");
    String[] tokens =
        new String[] {
          "order_id",
          "first_name",
          "last_name",
          "email",
          "cost",
          "item_id",
          "item_name",
          "ship_date"
        };
    tokenizer.setNames(tokens);
    lineMapper.setLineTokenizer(tokenizer);
    lineMapper.setFieldSetMapper(new OrderFieldSetMapper());

    itemReader.setLineMapper(lineMapper);
    return itemReader;
  }
}
