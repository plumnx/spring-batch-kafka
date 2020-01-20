package com.plumnix.cloud.flow.testdb;

//@Configuration
//@EnableKafka
public class KafkaProducerConfig {

//    bootstrap-servers: 192.168.3.13:9092
//    key-serializer: org.apache.kafka.common.serialization.StringSerializer
//    value-serializer: org.springframework.kafka.support.serializer.JsonSerializer
//    client-id: customers-client
//    batch.size: 16384
//    properties:
//    max.request.size: 52428800
//            #      buffer-memory: 10485760‬

//    public Map<String, Object> producerConfigs() {
//        Map<String, Object> props = new HashMap<>();
//        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.3.13:9092");
//        props.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);
////        props.put(ProducerConfig.LINGER_MS_CONFIG, linger);
//        props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, bufferMemory);
//        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
//        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
//        return props;
//    }
//
//    public ProducerFactory<String, String> producerFactory() {
//        return new DefaultKafkaProducerFactory<>(producerConfigs());
//    }
//
//    @Bean
//    public KafkaTemplate<String, String> kafkaTemplate() {
//        return new KafkaTemplate<String, String>(producerFactory());
//    }

}
