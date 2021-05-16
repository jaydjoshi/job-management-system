package com.dd.jobmangementsystem.batch.listener;

import com.dd.jobmangementsystem.batch.constants.QueryConstants;
import com.dd.jobmangementsystem.batch.model.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class TransactionJobCompletionNotificationListener extends JobExecutionListenerSupport {

    private static final Logger log = LoggerFactory.getLogger(TransactionJobCompletionNotificationListener.class);

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public TransactionJobCompletionNotificationListener(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        if(jobExecution.getStatus() == BatchStatus.COMPLETED) {
            log.info("JOB FINISHED! Below are the results");

            jdbcTemplate.query(QueryConstants.SELECT_PERSON_QUERY,
                    (rs, row) -> new Person(
                            rs.getInt(1),
                            rs.getString(2),
                            rs.getString(3))
            ).forEach(person -> log.info("Found <" + person + "> in the database."));
        }
    }
}