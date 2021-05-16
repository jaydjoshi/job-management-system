# Job Management system in spring batch

1. To clean install,
`mvn clean install`

2. To run all the jobs, `./mvnw spring:boot run`. It will run all the `@Scheduled` jobs and all one time jobs.

## Architecture

This system heavily uses SPring batch architecture,

![Alt text](/src/main/resources/image/spring-batch-reference-model.png?raw=true "Spring batch architecture")

## Jobs
Below are the jobs,

#### Person job
Person job has one step and below mentioned details of reader, writer and processor,
1. **reader** - Read csv file person-data.csv
2. **processor** - Process Person into Person with first name and last name in uppercase
3. **writer** - Write List into H2 database
Scheduled - ( */10 * * * * * ) Reoccurring job every 10 secs

#### Transaction job
Transaction job has two steps,

**Step 1**
1. **reader** - Read csv file transaction-data.csv
2. **processor** - Process Transaction into Transaction with amount updated.
3. **writer** - Write List into H2 database
**Step 2**
1. Tasklet to print logger. (Can be used to send mails)
Scheduled - ( */10 * * * * * ) Reoccurring job every 10 secs

#### Random Number job
Random Number Job has one below step,
1. Tasklet based step to print random number.
Scheduled - Run immediately once after start up. 


All Jobs use Spring batch Java annotation configuration.

Spring scheduler is used to schedule jobs.

As Spring batch already maintains any of the below execution status,
- COMPLETED
- STARTING
- STARTED
- STOPPING
- STOPPED
- FAILED
- ABANDONED
- UNKNOWN

Defined in class BatchStatus
I have not created a separate table to store the status of Job like COMPLETED, QUEUED, RUNNING, FAILED

## Access H2 console
http://localhost:8080/h2-console
- username : sa
- password : 

## Queries to access Spring batch job and step tables
1. SELECT * FROM BATCH_JOB_EXECUTION ;
2. SELECT * FROM BATCH_JOB_EXECUTION_CONTEXT ;
3. SELECT * FROM BATCH_JOB_EXECUTION_PARAMS ;
4. SELECT * FROM BATCH_JOB_INSTANCE ;
5. SELECT * FROM BATCH_STEP_EXECUTION ;
6. SELECT * FROM BATCH_STEP_EXECUTION_CONTEXT ;

Read more about the tables https://docs.spring.io/spring-batch/docs/current/reference/html/schema-appendix.html.

## Queries to access Job tables
1. SELECT * FROM PEOPLE ;
2. SELECT * FROM TRANSACTION_DETAIL ;

## Features

1. Flexibility - New jobs can easily be added to this system. All the existing jobs support Spring batch and JSR-352 specification.
2. Reliability - If job fails then rollback steps can be added JobListener. (Have not added it)
3. Internal Consistency - Each job maintains a status as defined in BatchStatus enum.
4. Priority - Jobs can be ran in parallel using Thread pool. Size is configurable in application.properties. However, if multiple jobs start at same time then they are executed as per method ordering.
5. Scheduling - Jobs are scheduled using spring scheduler.

## Notes
1. Rollback steps in case of Job/ Step failure can be written in JobListener/ StepListener.

## Assumptions
1. Jobs that we want to run can be run in a single JVM instance. This system does not support distributed system jobs.

## Point to improve 
- If we want to run distributed system jobs like Apache spark, then, we can use external schedule and create shell scripts to wrap the calls of either spark-submit or spring batch.
- Running multiple jobs scheduled at same time with priority would require us to create our own framework using PriorityBlockingQueue and Executor service to execute jobs. This would require creating an entire framework.