package com.plumnix.cloud.flow.consumer.api;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/")
public class WebController implements ApplicationContextAware {

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