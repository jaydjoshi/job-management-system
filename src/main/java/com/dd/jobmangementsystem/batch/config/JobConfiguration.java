package com.dd.jobmangementsystem.batch.config;

import com.dd.jobmangementsystem.batch.job.PersonJobDetail;
import com.dd.jobmangementsystem.batch.job.TransactionDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.support.JobRegistryBeanPostProcessor;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobOperator;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.batch.support.DatabaseType;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableScheduling
@EnableBatchProcessing
public class JobConfiguration {

	private final static Logger LOGGER =
			LoggerFactory.getLogger(JobConfiguration.class);

	@Value("${spring.executor.core.pool.size}")
	private int corePoolSize;
	@Value("${spring.executor.max.pool.size}")
	private int maxPoolSize;
	@Value("${spring.executor.queue.capacity}")
	private int queueCapacity;

	@Autowired
	private JobExplorer jobExplorer;

	@Autowired
	private TransactionDetail transactionDetail;

	@Autowired
	private PersonJobDetail personJobDetail;

	@Bean
	public ResourcelessTransactionManager transactionManager() {
		return new ResourcelessTransactionManager();
	}

	/**
	 * Tried Setting Queue as PriorityBlockingQueue cause we needed to execute tasks based on priority
	 * However, Order by priority not working because
	 *
	 * If multiple jobs needs to be ran at same time, they will be catered using executor thread pool.
	 * Current order execution is based on method order in class
	 *
	 * @return
	 */
	@Bean
	public TaskExecutor taskExecutor() {
		ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
		taskExecutor.setCorePoolSize(corePoolSize);
		taskExecutor.setMaxPoolSize(maxPoolSize);
		taskExecutor.setQueueCapacity(queueCapacity);
		return taskExecutor;
	}

	@Bean
	public JobLauncher jobLauncher() throws Exception {
		SimpleJobLauncher jobLauncher = new SimpleJobLauncher();
		jobLauncher.setTaskExecutor(taskExecutor());
		// jobLauncher.setTaskExecutor(new SimpleAsyncTaskExecutor());
		jobLauncher.setJobRepository(jobRepository());
		jobLauncher.afterPropertiesSet();
		return jobLauncher;
	}

	@Bean
	public JobOperator jobOperator(JobRegistry jobRegistry) throws Exception {
		SimpleJobOperator jobOperator = new SimpleJobOperator();
		jobOperator.setJobExplorer(jobExplorer);
		jobOperator.setJobLauncher(jobLauncher());
		jobOperator.setJobRegistry(jobRegistry);
		jobOperator.setJobRepository(jobRepository());
		return jobOperator;
	}

	@Bean
	public JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor(JobRegistry jobRegistry) {
		JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor = new JobRegistryBeanPostProcessor();
		jobRegistryBeanPostProcessor.setJobRegistry(jobRegistry);
		return jobRegistryBeanPostProcessor;
	}

	@Bean
	public DataSource dataSource() {
		EmbeddedDatabaseBuilder embeddedDatabaseBuilder = new EmbeddedDatabaseBuilder();
		return embeddedDatabaseBuilder.addScript("classpath:org/springframework/batch/core/schema-drop-h2.sql")
				.addScript("classpath:org/springframework/batch/core/schema-h2.sql")
				.setType(EmbeddedDatabaseType.H2)
				.build();
	}

	@Bean
	public JobRepository jobRepository() throws Exception {
		JobRepositoryFactoryBean factory = new JobRepositoryFactoryBean();
		factory.setDatabaseType(DatabaseType.H2.getProductName());
		factory.setDataSource(dataSource());
		factory.setTransactionManager(transactionManager());
		return factory.getObject();
	}

	/**
	 * Run persons job
	 * Scheduled every 10 secs
	 */
	@Scheduled(cron = "*/10 * * * * *")
	public void runPersonJob() {
		Map<String, JobParameter> confMap = new HashMap<>();
		confMap.put("time", new JobParameter(System.currentTimeMillis()));
		confMap.put("priority", new JobParameter(1l));
		JobParameters jobParameters = new JobParameters(confMap);
		try {
			jobLauncher().run(personJobDetail.getPersonJob(), jobParameters);
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage());
		}
	}

	/**
	 * Run transactions job
	 * Scheduled every 10 secs
	 */
	@Scheduled(cron = "*/10 * * * * *")
	public void runTransactionJob() {
		Map<String, JobParameter> confMap = new HashMap<>();
		confMap.put("time", new JobParameter(System.currentTimeMillis()));
		confMap.put("priority", new JobParameter(2l));
		JobParameters jobParameters = new JobParameters(confMap);
		try {
			jobLauncher().run(transactionDetail.getTransactionJob(), jobParameters);
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage());
		}
	}


}
