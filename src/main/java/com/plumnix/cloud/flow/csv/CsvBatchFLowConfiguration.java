package com.plumnix.cloud.flow.csv;

import com.plumnix.cloud.flow.testdb.TestDbItemReader;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
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
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

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
    @StepScope
    public JobExecutionListener csvListener() {
        return new CsvListener();
    }

    @Bean
    public TaskExecutor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(10);
        taskExecutor.setMaxPoolSize(20);
        return taskExecutor;
    }

    @Bean
    Step csvStep() {
        TaskExecutor taskExecutor = new SimpleAsyncTaskExecutor();

        ItemProcessor csvItemProcessor = new CsvItemProcessor();
        return stepBuilderFactory
                .get("csvStep")
                .chunk(Integer.MAX_VALUE)
                .reader(new CsvItemReader())
//                .reader(new TestDbItemReader())
//                .reader(new CsvItemByIOUtilsReader())
                .processor(csvItemProcessor())
                .writer(new CsvItemWriter())
//                .taskExecutor(taskExecutor)
//                .taskExecutor(threadPoolTaskExecutor())
//                .throttleLimit(5)
                .build();
    }

    @Bean
    Job csvJob() {
        return jobBuilderFactory.get("csvJob")
                .incrementer(new RunIdIncrementer())
                .start(csvStep())
                .listener(new CsvListener())
                .build();

    }

}
