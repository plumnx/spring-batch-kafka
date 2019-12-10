package com.plumnix.cloud.flow;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static java.lang.Integer.MAX_VALUE;

@Configuration
public class CsvBatchFLowConfiguration {

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @StepScope
    @Bean
    public ItemReader csvItemReader() {
        return new CsvItemReader();
    }

    @StepScope
    @Bean
    public ItemProcessor csvItemProcessor() {
        return new CsvItemProcessor();
    }

    @StepScope
    @Bean
    public ItemWriter csvItemWriter() {
        return new CsvItemWriter();
    }

    @Bean
    Step csvStep() {
        return stepBuilderFactory
                .get("csvStep")
                .chunk(MAX_VALUE)
                .reader(csvItemReader())
                .processor(csvItemProcessor())
                .writer(csvItemWriter())
                .build();
    }

    @Bean
    Job csvJob() {
        return jobBuilderFactory.get("csvJob")
                .incrementer(new RunIdIncrementer())
                .start(csvStep())
                .build();

    }

}
