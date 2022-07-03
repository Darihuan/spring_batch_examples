package com.example.chunk_example_batch.content.Linkedin;

import com.example.chunk_example_batch.content.Linkedin.reader.SimpleItemReader;
import lombok.AllArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class LinkeinBatchProcessor {
  private final JobBuilderFactory jobBuilderFactory;
  private final StepBuilderFactory stepBuilderFactory;

  @Bean("try_chunk_job")
  public Job chunkJob(@Qualifier("try_chunk_step") Step chunkStep){
      return this.jobBuilderFactory
              .get("chunkBasedJob")
              .start(chunkStep)
              .build();
  }

  @Bean("try_chunk_step")
  public Step chunkBasedStep(@Qualifier("simple_reader") ItemReader<String> custoReader) {
    return this.stepBuilderFactory
        .get("chunkBasedStep")
        .<String, String>chunk(3)
        .reader(custoReader)
        .writer(
            (list) -> {
              System.out.printf("Received a list of size %s \n", list.size());
              list.forEach(System.out::println);
            })
        .build();
  }
  @Bean("simple_reader")
    public ItemReader<String> itemReader() {
      return new SimpleItemReader();
  }
}
