package com.plumnix.cloud.flow.csv;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableBatchProcessing
@SpringBootApplication
public class CsvBatchFLowApplication {

    public static void main(String[] args) {
        SpringApplication.run(CsvBatchFLowApplication.class, args);
    }

}
