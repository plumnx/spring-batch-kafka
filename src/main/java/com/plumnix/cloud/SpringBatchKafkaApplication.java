package com.plumnix.cloud;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableBatchProcessing
@SpringBootApplication
public class SpringBatchKafkaApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBatchKafkaApplication.class, args);
    }

}
