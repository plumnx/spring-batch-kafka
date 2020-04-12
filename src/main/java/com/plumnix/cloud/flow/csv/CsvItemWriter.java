package com.plumnix.cloud.flow.csv;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.item.ItemWriter;
import org.springframework.lang.Nullable;

import java.util.List;

public class CsvItemWriter implements ChunkListener, StepExecutionListener, ItemWriter<CsvItemProcessor.HeaderBody> {

    private StepExecution stepExecution;

    private ChunkContext chunkContext;

    @Override
    public void write(List<? extends CsvItemProcessor.HeaderBody> items) throws Exception {
//        System.out.println(Thread.currentThread().getId() + " " + Thread.currentThread().getName() + " chunk: " + this.chunkContext.getAttribute("read"));
        System.out.println(Thread.currentThread().getId() + " " + Thread.currentThread().getName() + " TestDbItemWriter: " + items.toString());
//        items.forEach(o -> {
//            try {
//                System.out.println(Thread.currentThread().getId() + " " + Thread.currentThread().getName() + " new "+ new ObjectMapper().writeValueAsString(o));
//            } catch (JsonProcessingException e) {
//                e.printStackTrace();
//            }
//        });
    }

    @Override
    public void beforeStep(StepExecution stepExecution) {
        this.stepExecution = stepExecution;
    }

    @Nullable
    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        return null;
    }

    @Override
    public void beforeChunk(ChunkContext context) {
        this.chunkContext = context;
    }

    @Override
    public void afterChunk(ChunkContext context) {

    }

    @Override
    public void afterChunkError(ChunkContext context) {

    }
}
