package com.dd.jobmangementsystem.batch.job;

import com.dd.jobmangementsystem.batch.constants.JobConstants;
import com.dd.jobmangementsystem.batch.constants.QueryConstants;
import com.dd.jobmangementsystem.batch.listener.JobListener;
import com.dd.jobmangementsystem.batch.listener.ReadListener;
import com.dd.jobmangementsystem.batch.listener.StepListener;
import com.dd.jobmangementsystem.batch.listener.WriteListener;
import com.dd.jobmangementsystem.batch.model.Transaction;
import com.dd.jobmangementsystem.batch.processor.TransactionItemProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import javax.sql.DataSource;

@Configuration
@EnableBatchProcessing
public class TransactionDetail {

    private final static Logger LOGGER =
            LoggerFactory.getLogger(TransactionDetail.class);

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
     * This is a dummy tasklet, not configured mail server to send mails
     * @return
     */
    @Bean
    public Step mailTasklet() {
        return stepBuilderFactory.get("mailTasklet")
                .listener(stepListener())
                .listener(readListener())
                .listener(writeListener())
                .tasklet((contribution, chunkContext) -> {
                    LOGGER.info("Tasklet for Transaction has run and mail sent!");
                    return RepeatStatus.FINISHED;
                }).build();
    }

    /**
     * Reader, writer and processor based job for transactions,
     * and send mail
     * @return Job
     */
    @Bean
    public Job getTransactionJob() {
        Step step = stepBuilderFactory.get(JobConstants.TRANSACTION_STEP)
                .listener(readListener())
                .listener(stepListener())
                .listener(writeListener())
                .<Transaction, Transaction> chunk(JobConstants.TRANSACTION_JOB_CHUNK_SIZE)
                .reader(transactionReader())
                .processor(transactionProcessor())
                .writer(transactionWriter())
                .build();
        try {
            Thread.sleep(3000l);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return jobBuilderFactory.get(JobConstants.TRANSACTION_JOB)
                .listener(jobListener())
                .start(step)
                .next(mailTasklet())
                .build();
    }

    @Bean
    public FlatFileItemReader<Transaction> transactionReader() {
        return new FlatFileItemReaderBuilder<Transaction>()
                .name(JobConstants.TRANSACTION_READER)
                .resource(new ClassPathResource(filePath))
                .delimited()
                .names(new String[]{"personId", "transactionDate", "amount"})
                .fieldSetMapper(new BeanWrapperFieldSetMapper<Transaction>() {{
                    setTargetType(Transaction.class);
                }})
                .build();
    }

    @Bean
    public TransactionItemProcessor transactionProcessor() {
        return new TransactionItemProcessor();
    }

    @Bean
    public JdbcBatchItemWriter<Transaction> transactionWriter(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<Transaction>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql(QueryConstants.TRANSACTION_QUERY)
                .dataSource(dataSource)
                .build();
    }

    @Bean
    public ItemWriter<Transaction> transactionWriter(){
        return new JdbcBatchItemWriter<Transaction>() ;
    }


}
