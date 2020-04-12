package com.plumnix.cloud.flow.csv;

import com.plumnix.cloud.flow.csv.entity.HeaderBodyTemplate;
import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.item.ItemReader;
import org.springframework.core.io.ClassPathResource;
import org.springframework.lang.Nullable;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;

public class CsvItemReader implements StepExecutionListener, ChunkListener, ItemReader<HeaderBodyTemplate.HeaderBodyItem> {

    private Iterator<String> iterator;

    private HeaderBodyTemplate headerBodyTemplate;

    private StepExecution stepExecution;

    private ChunkContext chunkContext;

    public CsvItemReader() {
        this.headerBodyTemplate = new HeaderBodyTemplate();
    }

    @Nullable
    @Override
    public synchronized HeaderBodyTemplate.HeaderBodyItem read() throws Exception {
        if(null == iterator) {
            List<String> lines = null;
            try {
                lines = Files.readAllLines(Paths.get(
                        new ClassPathResource("/test.csv").getURI()), StandardCharsets.UTF_8);
            } catch (IOException e) {
                e.printStackTrace();
            }
            this.iterator = lines.iterator();
        }
        if(this.iterator.hasNext()) {
            String line = this.iterator.next();
            HeaderBodyTemplate.HeaderBodyItem headerBodyItem = new HeaderBodyTemplate.HeaderBodyItem(line, headerBodyTemplate).header().body().count();
//            System.out.println(Thread.currentThread().getId() + " " + Thread.currentThread().getName() + " read " + headerBodyItem.toString());
            this.chunkContext.setAttribute("read", headerBodyItem.toString());
            return headerBodyItem;
        } else {
//            System.out.println(Thread.currentThread().getId() + " " + Thread.currentThread().getName() + " read ");
        }
        return null;
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
