package com.plumnix.cloud.flow;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Log4j2
@SpringBootApplication
@RequiredArgsConstructor
@EnableBatchProcessing
public class FlowBatchApplication {

    public static void main(String[] args) {
        SpringApplication.run(FlowBatchApplication.class, args);
    }

}
