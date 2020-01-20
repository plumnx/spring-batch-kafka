package com.plumnix.cloud.flow.producer;

import com.plumnix.cloud.flow.csv.entity.Customer;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.kafka.KafkaItemWriter;
import org.springframework.batch.item.kafka.builder.KafkaItemWriterBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.concurrent.atomic.AtomicLong;

//@Configuration
public class ProducerBatchConfiguration {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private KafkaTemplate<Long, Customer> template;

    @Bean
    Job job() {
        return this.jobBuilderFactory
                .get("job")
                .start(start())
                .incrementer(new RunIdIncrementer())
                .build();
    }

    @Bean
    KafkaItemWriter<Long, Customer> kafkaItemWriter() {
        return new KafkaItemWriterBuilder<Long, Customer>()
                .kafkaTemplate(template)
                .itemKeyMapper(Customer::getId)
                .build();
    }


    @Bean
    Step start() {

        AtomicLong id = new AtomicLong();
        ItemReader reader = new ItemReader<Customer>() {

            @Override
            public Customer read() {

                if (id.incrementAndGet() < 10)
                    return new Customer(id.get(), Math.random() > .5 ? "Jane" : "Jose");

                return null;
            }
        };

        return this.stepBuilderFactory
                .get("s1")
                .<Customer, Customer>chunk(10)
                .reader(reader)
                .writer(kafkaItemWriter())
                .build();
    }

}
