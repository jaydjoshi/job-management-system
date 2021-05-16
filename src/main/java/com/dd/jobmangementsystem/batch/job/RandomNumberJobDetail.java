package com.dd.jobmangementsystem.batch.job;

import com.dd.jobmangementsystem.batch.constants.JobConstants;
import com.dd.jobmangementsystem.batch.listener.JobListener;
import com.dd.jobmangementsystem.batch.listener.ReadListener;
import com.dd.jobmangementsystem.batch.listener.StepListener;
import com.dd.jobmangementsystem.batch.listener.WriteListener;
import com.dd.jobmangementsystem.batch.model.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableBatchProcessing
public class RandomNumberJobDetail {

    private final static Logger LOGGER =
            LoggerFactory.getLogger(RandomNumberJobDetail.class);

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;


    @Value("${batch.transaction.reader.file}")
    private String filePath;

    @Bean
    public ItemReadListener<Transaction> readListener() {
        return new ReadListener<Transaction>();
    }

    @Bean
    public StepExecutionListener stepListener() {
        return new StepListener();
    }

    @Bean
    public ItemWriteListener<Transaction> writeListener() {
        return new WriteListener<Transaction>();
    }

    @Bean
    public JobExecutionListener jobListener() {
        return new JobListener();
    }

    /**
     * This is a Random number generator
     * @return
     */
    @Bean
    public Step randomNumberTasklet() {
        return stepBuilderFactory.get("randomNumberTasklet")
                .listener(stepListener())
                .listener(readListener())
                .listener(writeListener())
                .tasklet((contribution, chunkContext) -> {
                    LOGGER.info("Tasklet for Random number generator has run :" + Math.random() * 1000);
                    return RepeatStatus.FINISHED;
                }).build();
    }

    /**
     *
     * @return Job
     */
    @Bean
    public Job getRandomNumberJob() {

        try {
            Thread.sleep(1000l);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return jobBuilderFactory.get(JobConstants.RANDOM_NUMBER_JOB)
                .listener(jobListener())
                .start(randomNumberTasklet())
                .build();
    }


}
