package com.plumnix.cloud.consumer;

import com.plumnix.cloud.entity.Customer;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.var;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.kafka.KafkaItemReader;
import org.springframework.batch.item.kafka.builder.KafkaItemReaderBuilder;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Properties;

@Log4j2
@SpringBootApplication
@RequiredArgsConstructor
@EnableBatchProcessing
public class ConsumerApplication {

    public static void main(String args[]) {
        SpringApplication.run(ConsumerApplication.class, args);
    }

    @RestController("/")
    public static class WebController implements ApplicationContextAware {

        @Autowired
        private Step start;

        @Autowired
        private JobBuilderFactory jobBuilderFactory;

        @Autowired
        private JobLauncher jobLauncher;

        @GetMapping
        public void init() throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {

            ConfigurableApplicationContext applicationContext = (ConfigurableApplicationContext) this.applicationContext;
            DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) applicationContext.getBeanFactory();

            AbstractBeanDefinition beanDefinition = BeanDefinitionBuilder.genericBeanDefinition(Job.class, () -> {
                return jobBuilderFactory.get("job")
                        .incrementer(new RunIdIncrementer())
                        .start(start)
                        .build();
            }).getBeanDefinition();
            beanFactory.registerBeanDefinition("job", beanDefinition);
//
            Job job = (Job) this.applicationContext.getBean("job");
            jobLauncher.run(job, new JobParameters());
        }

        private ApplicationContext applicationContext;

        @Override
        public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
            this.applicationContext = applicationContext;
        }
    }

    @Configuration
    public static class ConsumerBatchConfiguration {

        @Autowired
        private KafkaProperties properties;
        @Autowired
        private StepBuilderFactory stepBuilderFactory;
        @Autowired
        private JobBuilderFactory jobBuilderFactory;

        @Bean
        Job job() {
            return jobBuilderFactory.get("job")
                    .incrementer(new RunIdIncrementer())
                    .start(start())
                    .build();

        }

        @Bean
        KafkaItemReader<Long, Customer> kafkaItemReader() {
            var props = new Properties();
            props.putAll(this.properties.buildConsumerProperties());

            return new KafkaItemReaderBuilder<Long, Customer>()
                    .partitions(0)
                    .consumerProperties(props)
                    .name("customers-reader")
                    .saveState(true)
                    .topic("customers_new")
                    .build();
        }

        @Bean
        Step start() {
            var writer = new ItemWriter<Customer>() {
                @Override
                public void write(List<? extends Customer> items) throws Exception {
                    items.forEach(it -> log.info("new customer: " + it));
                }
            };
            return stepBuilderFactory
                    .get("step")
                    .<Customer, Customer>chunk(10)
                    .writer(writer)
                    .reader(kafkaItemReader())
                    .build();
        }

    }

}