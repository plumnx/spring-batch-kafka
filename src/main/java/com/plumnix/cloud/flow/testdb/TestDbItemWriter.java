package com.plumnix.cloud.flow.testdb;

import com.plumnix.cloud.flow.testdb.entity.Output;
import org.springframework.batch.item.ItemWriter;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.List;
import java.util.Map;

public class TestDbItemWriter implements ItemWriter<Map> {

    private KafkaTemplate kafkaTemplate;

    public TestDbItemWriter(KafkaTemplate kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void write(List<? extends Map> items) throws Exception {
        this.kafkaTemplate.sendDefault("test", Output.builder().items(items).size(items.size()).build());
//        System.out.println(Output.builder().items(items).size(items.size()).build());
    }

}
