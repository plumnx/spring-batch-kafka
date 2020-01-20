package com.plumnix.cloud;

import com.plumnix.cloud.flow.csv.entity.Customer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
//@SpringBootTest(classes = ProducerApplication.class)
@EnableAutoConfiguration
public class SpringBootKafkaTest {

    @Autowired
    private KafkaTemplate<Long, Customer> kafkaTemplate;

    @Test
    public void test() {
        kafkaTemplate.send(
                "customers_new", 123L, new Customer(123L, "Alice"));
    }

}
