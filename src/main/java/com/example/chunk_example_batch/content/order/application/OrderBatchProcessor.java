package com.example.chunk_example_batch.content.order.application;

import com.example.chunk_example_batch.configuration.ErrorProcessingExpection;
import com.example.chunk_example_batch.content.TrackedOrder.domain.TrackedOrder;
import com.example.chunk_example_batch.content.order.application.itemProcessors.FreeShippingItemProcessor;
import com.example.chunk_example_batch.content.order.application.itemProcessors.TrackOrderItemProcessor;
import com.example.chunk_example_batch.content.order.application.mappers.OrderFieldSetMapper;
import com.example.chunk_example_batch.content.order.application.mappers.OrderRowMapper;
import com.example.chunk_example_batch.content.order.application.slipListener.CustomRetryListener;
import com.example.chunk_example_batch.content.order.domain.Order;
import lombok.AllArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.json.JacksonJsonObjectMarshaller;
import org.springframework.batch.item.json.builder.JsonFileItemWriterBuilder;
import org.springframework.batch.item.support.builder.CompositeItemProcessorBuilder;
import org.springframework.batch.item.validator.BeanValidatingItemProcessor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
@AllArgsConstructor
public class OrderBatchProcessor {
  private final JobBuilderFactory jobBuilderFactory;
  private final StepBuilderFactory stepBuilderFactory;
  private DataSource dataSource;

  @Bean("order_job")
  private Job orderJob(@Qualifier("csv_step") Step csvStep) {
    return this.jobBuilderFactory.get("order_job").start(csvStep).build();
  }

  @Bean("csv_step")
  public Step csvChunkStep(
      @Qualifier("order_reader_jdbc_paged") ItemReader<Order> orderReader,
      @Qualifier("compose_itemprocessor") ItemProcessor<Order, TrackedOrder> itemProcessor,
      @Qualifier("jdbc_Trackedorder_writer") ItemWriter<TrackedOrder> itemWriter) {
    return this.stepBuilderFactory
        .get("CSV_step")
        .<Order, TrackedOrder>chunk(30)
        .reader(orderReader)
        .processor(itemProcessor)
            .faultTolerant()
            .retry(ErrorProcessingExpection.class)
            .retryLimit(3)
            .listener(new CustomRetryListener())
        .writer(itemWriter)
        .build();
  }
  //  @Bean("csv_step")
  //  public Step csvChunkStep(@Qualifier("order_reader_jdbc_paged") ItemReader<Order> orderReader)
  // {
  //    return this.stepBuilderFactory
  //        .get("CSV_step")
  //        .<Order, Order>chunk(30)
  //        .reader(orderReader)
  //        .writer(
  //            (list) -> {
  //              System.out.printf("Received a list of size %s \n", list.size());
  //              list.forEach(System.out::println);
  //            })
  //        .build();
  //  }

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
  // inicio Readers
  @Bean("order_reader_jdbc")
  public ItemReader<Order> itemReaderJdbc() {
    return new JdbcCursorItemReaderBuilder<Order>()
        .dataSource(this.dataSource)
        .name("jdbcOrderDataSource")
        .sql(
            "SELECT order_id,first_name,last_name,email,cost,item_id,item_name,"
                + "ship_date FROM SHIPPED_ORDER order by order_id")
        .rowMapper(new OrderRowMapper())
        .build();
  }

  @Bean("order_reader_jdbc_paged")
  public ItemReader<Order> itemReaderJdbcPaged(
      @Qualifier("query_provider") PagingQueryProvider queryProvider) {
    return new JdbcPagingItemReaderBuilder<Order>()
        .dataSource(this.dataSource)
        .name("jdbcOrderDataSource")
        .queryProvider(queryProvider)
        .rowMapper(new OrderRowMapper())
        .pageSize(30)
        .build();
  }

  @Bean("query_provider")
  public PagingQueryProvider queryProvider() throws Exception {
    SqlPagingQueryProviderFactoryBean factory = new SqlPagingQueryProviderFactoryBean();
    factory.setSelectClause(
        "SELECT order_id,first_name,last_name,email,cost,item_id,item_name,ship_date");
    factory.setFromClause("FROM SHIPPED_ORDER");
    factory.setSortKey("order_id");
    factory.setDataSource(this.dataSource);
    return factory.getObject();
  }

  // final Readers

  // inicio ItemWriters

  @Bean("file_order_writer")
  public ItemWriter<Order> itemWriter() {
    FlatFileItemWriter<Order> itemWriter = new FlatFileItemWriter<>();
    itemWriter.setResource(new FileSystemResource("./data/shipped_orders_output.csv"));

    DelimitedLineAggregator<Order> csvFormatter = new DelimitedLineAggregator<>();
    csvFormatter.setDelimiter(",");

    String[] tokens =
        new String[] {
          "orderId", "firstName", "lastName", "email", "cost", "itemId", "itemName", "shipDate"
        };
    BeanWrapperFieldExtractor<Order> fieldExtractor = new BeanWrapperFieldExtractor<>();
    fieldExtractor.setNames(tokens);

    csvFormatter.setFieldExtractor(fieldExtractor);

    itemWriter.setLineAggregator(csvFormatter);
    return itemWriter;
  }

  @Bean("jdbc_order_writer")
  public ItemWriter<Order> jdbcItemWriter() {
    return new JdbcBatchItemWriterBuilder<Order>()
        .dataSource(this.dataSource)
        .sql(
            "INSERT INTO  TRACKED_ORDER(order_id,first_name,last_name,email,cost,item_id,item_name,ship_date) "
                + "values (:orderId,:firstName,:lastName,:email,:cost,:itemId,:itemName,:shipDate)")
        .beanMapped()
        .build();
  }

  @Bean("jdbc_Trackedorder_writer")
  public ItemWriter<TrackedOrder> jdbcTrackedOrderItemWriter() {
    return new JdbcBatchItemWriterBuilder<TrackedOrder>()
        .dataSource(this.dataSource)
        .sql(
            "INSERT INTO  TRACKED_ORDER(order_id,first_name,last_name,email,cost,item_id,item_name,ship_date,tracking_number,free_shipping) "
                + "values (:orderId,:firstName,:lastName,:email,:cost,:itemId,:itemName,:shipDate,:trackingNumber,:freeShipping)")
        .beanMapped()
        .build();
  }

  @Bean("json_order_writer")
  public ItemWriter<Order> jsonItemWriter() {
    return new JsonFileItemWriterBuilder<Order>()
        .jsonObjectMarshaller(new JacksonJsonObjectMarshaller<>())
        .resource(new FileSystemResource("./data/order_output.json"))
        .name("jsonOrderWriter")
        .build();
  }

  // fin writers

  // inicio Item Processors
  @Bean("validation_processor")
  public ItemProcessor<Order, Order> orderValidatingItemProcessor() {
    BeanValidatingItemProcessor<Order> itemProcessor = new BeanValidatingItemProcessor<>();
    itemProcessor.setFilter(true);
    return itemProcessor;
  }

  @Bean("trackedOrdered_itemProcessor")
  public ItemProcessor<Order, TrackedOrder> trackedOrderItemProcessor() {
    return new TrackOrderItemProcessor();
  }

  @Bean("compose_itemprocessor")
  ItemProcessor<Order, TrackedOrder> composeItemProcessor(
      @Qualifier("validation_processor") ItemProcessor<Order, Order> validationProcessor,
      @Qualifier("trackedOrdered_itemProcessor")
          ItemProcessor<Order, TrackedOrder> trackedOrderProccessor,
      @Qualifier("free-shipping_itemProcessors")
          ItemProcessor<TrackedOrder, TrackedOrder> freeShippingProcessor) {

    return new CompositeItemProcessorBuilder<Order, TrackedOrder>()
        .delegates(validationProcessor, trackedOrderProccessor, freeShippingProcessor)
        .build();
  }

  @Bean("free-shipping_itemProcessors")
  ItemProcessor<TrackedOrder, TrackedOrder> freeShippingITemProcessor() {
    return new FreeShippingItemProcessor();
  }
}
