package com.plumnix.cloud.flow;

import com.plumnix.cloud.entity.HeaderBodyTemplate;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemStream;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.lang.Nullable;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;

public class CsvItemByIOUtilsReader implements ItemStream, StepExecutionListener, ChunkListener, ItemReader<HeaderBodyTemplate.HeaderBodyItem> {

    private Iterator<String> iterator;

    private HeaderBodyTemplate headerBodyTemplate;

    private StepExecution stepExecution;

    private ChunkContext chunkContext;

    private LineIterator lineIterator;

    public CsvItemByIOUtilsReader() {
        this.headerBodyTemplate = new HeaderBodyTemplate();
    }


    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        try {
            Path path = Paths.get(new ClassPathResource("/test_701.csv").getURI());
            this.lineIterator = IOUtils.lineIterator(new FileReader(path.toFile()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public synchronized HeaderBodyTemplate.HeaderBodyItem read() throws Exception {
        if(!lineIterator.hasNext()) {
            return null;
        }

        HeaderBodyTemplate.HeaderBodyItem headerBodyItem = new HeaderBodyTemplate.HeaderBodyItem(lineIterator.nextLine(), headerBodyTemplate).header().body().count();
        this.chunkContext.setAttribute("read", headerBodyItem.toString());
        return headerBodyItem;
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

    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {

    }

    @Override
    public void close() throws ItemStreamException {
        try {
            lineIterator.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}