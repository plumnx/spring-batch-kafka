package com.plumnix.cloud.flow.csv;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;

import java.time.Duration;
import java.time.Instant;

public class CsvListener implements JobExecutionListener {

    private Instant now;

    @Override
    public void beforeJob(JobExecution jobExecution) {
        now = Instant.now();
        System.out.println("============= job duration start: " + now.toString());
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        Instant that = Instant.now();
        System.out.println("============= job duration end: " + that.toString());
        System.out.println("============= job duration: " + Duration.between(this.now, that).getSeconds());
    }
}
