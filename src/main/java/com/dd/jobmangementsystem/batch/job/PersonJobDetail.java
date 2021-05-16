package com.dd.jobmangementsystem.batch.job;




import com.dd.jobmangementsystem.batch.constants.JobConstants;
import com.dd.jobmangementsystem.batch.constants.QueryConstants;
import com.dd.jobmangementsystem.batch.listener.*;
import com.dd.jobmangementsystem.batch.listener.StepListener;
import com.dd.jobmangementsystem.batch.model.Person;
import com.dd.jobmangementsystem.batch.processor.PersonItemProcessor;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
@EnableBatchProcessing
public class PersonJobDetail {


    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    public JdbcTemplate jdbcTemplate;

    @Value("${batch.person.reader.file}")
    private String filePath;

    @Bean
    public FlatFileItemReader<Person> personReader() {
        return new FlatFileItemReaderBuilder<Person>()
                .name(JobConstants.PERSON_READER)
                .resource(new ClassPathResource(filePath))
                .delimited()
                .names(new String[]{"id", "firstName", "lastName"})
                .fieldSetMapper(new BeanWrapperFieldSetMapper<Person>() {{
                    setTargetType(Person.class);
                }})
                .build();
    }

    @Bean
    public PersonItemProcessor personProcessor() {
        return new PersonItemProcessor();
    }

    @Bean
    public PersonJobCompletionNotificationListener personListener(){
        return new PersonJobCompletionNotificationListener(jdbcTemplate);
    }

    @Bean
    public JdbcBatchItemWriter<Person> personWriter(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<Person>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql(QueryConstants.PERSON_QUERY)
                .dataSource(dataSource)
                .build();
    }

    @Bean
    public ItemWriter<Person> personWriter(){
        return new JdbcBatchItemWriter<Person>() ;
    }

    @Bean
    public ItemReadListener<Person> readListener() {
        return new ReadListener<Person>();
    }

    @Bean
    public StepExecutionListener stepListener() {
        return new StepListener();
    }

    @Bean
    public ItemWriteListener<Person> writeListener() {
        return new WriteListener<Person>();
    }

    @Bean
    public JobExecutionListener jobListener() {
        return new JobListener();
    }


    /**
     * Reader writer and processor based job,
     * read from csv, process and write to db for persons
     * @return
     */
    @Bean
    public Job getPersonJob() {
        Step step = stepBuilderFactory.get(JobConstants.PERSON_STEP)
                .listener(readListener())
                .listener(stepListener())
                .listener(writeListener())
                .<Person, Person> chunk(JobConstants.PERSON_JOB_CHUNK_SIZE)
                .reader(personReader())
                .processor(personProcessor())
                .writer(personWriter()).build();
        try {
            Thread.sleep(2000l);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return jobBuilderFactory.get(JobConstants.PERSON_JOB).listener(jobListener()).start(step).build();
    }
}
