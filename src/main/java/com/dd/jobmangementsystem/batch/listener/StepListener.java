package com.dd.jobmangementsystem.batch.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.stereotype.Component;

@Component
public class StepListener implements StepExecutionListener {

	private final static Logger LOGGER =
			LoggerFactory.getLogger(StepListener.class);
	@Override
	public void beforeStep(StepExecution stepExecution) {
		LOGGER.info("StepListener::beforeStep() -> Step " + stepExecution.getStepName() + " completed for "
				+ stepExecution.getJobExecution().getJobInstance().getJobName());
	}

	/**
	 * Custom Step failure rollbacks can be written in this method
	 * @param stepExecution
	 * @return
	 */
	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		LOGGER.info("StepListener::afterStep() -> Step " + stepExecution.getStepName() + " started for "
				+ stepExecution.getJobExecution().getJobInstance().getJobName());

		return null;
	}

}
