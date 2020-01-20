package com.plumnix.cloud.flow.testdb;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemStream;
import org.springframework.batch.item.ItemStreamSupport;
import org.springframework.core.io.ClassPathResource;
import org.springframework.lang.Nullable;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class TestDbItemReader extends ItemStreamSupport implements ItemReader<String>, ItemStream {

    private BufferedReader bufferedReader;

    @Override
    public void open(ExecutionContext executionContext) {
        super.open(executionContext);
        try {
            bufferedReader = new BufferedReader(new FileReader(new ClassPathResource("/test_458.csv").getFile()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public synchronized String read() throws Exception {
        String dataLine;
        while ((dataLine = bufferedReader.readLine()) != null) {
            return dataLine;
        }
        return null;
    }

    @Override
    public void close() {
        if (null != bufferedReader) {
            try {
                bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
