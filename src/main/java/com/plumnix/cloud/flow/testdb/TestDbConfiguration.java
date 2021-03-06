package com.plumnix.cloud.flow.testdb;

import com.plumnix.cloud.flow.csv.CsvListener;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.builder.FlowJobBuilder;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Map;

@Configuration
public class TestDbConfiguration {

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private KafkaTemplate kafkaTemplate;

    @Bean
    public TaskExecutor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(50);
        taskExecutor.setMaxPoolSize(50);
        return taskExecutor;
    }

    @Bean
    Step testDbStep() {
        return stepBuilderFactory
                .get("testDbStep")
                .<String, Map>chunk(5000)
                .reader(new TestDbItemReader())
                .processor(new TestDbItemProcessor())
                .writer(new TestDbItemWriter(kafkaTemplate))
                .taskExecutor(threadPoolTaskExecutor())
                .throttleLimit(200)
                .build();
    }

    @Bean
    Job testDbJob() {
        JobBuilder jobBuilder = jobBuilderFactory.get("testDbJob")
                .incrementer(new RunIdIncrementer());
        FlowBuilder<FlowJobBuilder> flowBuilder =
                jobBuilder.start(testDbStep()).on("SUCCESS").to(testDbStep());
        flowBuilder = flowBuilder.from(testDbStep()).on("1").to(testDbStep());
        flowBuilder = flowBuilder.from(testDbStep()).on("2").to(testDbStep());
        flowBuilder = flowBuilder.from(testDbStep()).on("3").to(testDbStep());
        FlowJobBuilder end = flowBuilder.end();
        Job job = end.listener(new CsvListener()).build();

        return jobBuilderFactory.get("testDbJob")
                .incrementer(new RunIdIncrementer())
                .start(testDbStep())
                .listener(new CsvListener())
                .build();

    }

}
