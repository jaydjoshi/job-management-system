# Job Management system in spring batch

1. To clean install,
`mvn clean install`

2. To run all the jobs, `./mvnw spring:boot run`. It will run all the `@Scheduled` jobs and all one time jobs.

3. Run job adhoc
java -jar ./target/job-management-system-0.0.1-SNAPSHOT.jar -Dspring.batch.job.names=runPersonJob

## Jobs
Jobs that are defined in spring batch based Java annotation configuration.
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
I have not created a seperate table to store the status of Job like COMPLETED, QUEUED, RUNNING, FAILED

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

## Notes
1. Rollback steps in case of Job/ Step failure can be written in JobListener/ StepListener.

## Assumptions
1. Jobs that we want to run can be run in a single JVM instance. This system does not support distributed system jobs.
