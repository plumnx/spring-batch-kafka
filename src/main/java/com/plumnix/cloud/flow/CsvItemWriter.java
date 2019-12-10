package com.plumnix.cloud.flow;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.batch.item.ItemWriter;

import java.util.List;

public class CsvItemWriter implements ItemWriter<CsvItemProcessor.HeaderBody> {

    @Override
    public void write(List<? extends CsvItemProcessor.HeaderBody> items) throws Exception {
        items.forEach(o -> {
            try {
                System.out.println(new ObjectMapper().writeValueAsString(o));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        });
    }

}
