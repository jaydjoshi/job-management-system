package com.dd.jobmangementsystem.batch.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.beans.factory.annotation.Autowired;

public class JobListener implements JobExecutionListener {

	private final static Logger LOGGER =
			LoggerFactory.getLogger(JobListener.class);

	private JobExecution active;

	@Autowired
	private JobOperator jobOperator;

	@Override
	public void beforeJob(JobExecution jobExecution) {
		final String jobName = jobExecution.getJobInstance().getJobName();
		final BatchStatus jobStatus = jobExecution.getStatus();

		LOGGER.info("JobListener::beforeJob() -> jobExecution: " + jobName + ", " + jobStatus.getBatchStatus());

	}

	/**
	 * Custom rollback steps can be written in this method for Job failure
	 * @param jobExecution
	 */
	@Override
	public void afterJob(JobExecution jobExecution) {
		final String jobName = jobExecution.getJobInstance().getJobName();
		final BatchStatus jobStatus = jobExecution.getStatus();

		LOGGER.info("JobListener::afterJob() -> jobExecution: " + jobName + ", " + jobStatus.getBatchStatus());

	}

}
