package com.example.chunk_example_batch.content.order.application;

import com.example.chunk_example_batch.content.order.application.mappers.OrderFieldSetMapper;
import com.example.chunk_example_batch.content.order.application.mappers.OrderRowMapper;
import com.example.chunk_example_batch.content.order.domain.Order;
import lombok.AllArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
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
  public Step csvChunkStep(@Qualifier("order_reader_jdbc_paged") ItemReader<Order> orderReader) {
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
  public ItemReader<Order> itemReaderJdbcPaged(@Qualifier("query_provider")PagingQueryProvider queryProvider) {
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
    factory.setSelectClause("SELECT order_id,first_name,last_name,email,cost,item_id,item_name,ship_date");
    factory.setFromClause("FROM SHIPPED_ORDER");
    factory.setSortKey("order_id");
    factory.setDataSource(this.dataSource);
    return factory.getObject();
  }


}
