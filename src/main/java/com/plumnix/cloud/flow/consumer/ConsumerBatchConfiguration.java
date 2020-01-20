package com.plumnix.cloud.flow.consumer;

import com.plumnix.cloud.flow.csv.entity.Customer;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.kafka.KafkaItemReader;
import org.springframework.batch.item.kafka.builder.KafkaItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Properties;

@Slf4j
//@Configuration
public class ConsumerBatchConfiguration {

    @Autowired
    private KafkaProperties properties;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Bean
    Job job() {
        return jobBuilderFactory.get("job")
                .incrementer(new RunIdIncrementer())
                .start(start())
                .build();

    }

    @Bean
    KafkaItemReader<Long, Customer> kafkaItemReader() {
        var props = new Properties();
        props.putAll(this.properties.buildConsumerProperties());

        return new KafkaItemReaderBuilder<Long, Customer>()
                .partitions(0)
                .consumerProperties(props)
                .name("customers-reader")
                .saveState(true)
                .topic("customers_new")
                .build();
    }

    @Bean
    Step start() {
        var writer = new ItemWriter<Customer>() {
            @Override
            public void write(List<? extends Customer> items) throws Exception {
                items.forEach(it -> log.info("new customer: " + it));
            }
        };
        return stepBuilderFactory
                .get("step")
                .<Customer, Customer>chunk(10)
                .writer(writer)
                .reader(kafkaItemReader())
                .build();
    }


}
