package com.vicastro.walletservice.infra.service.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;

@Service
public class DailySettlementScheduler {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final JobLauncher jobLauncher;
    private final Job dailySettlementJob;
    private final ZoneId APP_ZONE = ZoneId.of("America/Sao_Paulo");

    public DailySettlementScheduler(JobLauncher jobLauncher, Job dailySettlementJob) {
        this.jobLauncher = jobLauncher;
        this.dailySettlementJob = dailySettlementJob;
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void runDailySettlementJob() {
        var yesterday = LocalDate.now(APP_ZONE).minusDays(1);
        var jobParameters = new JobParametersBuilder()
                .addString("referenceDate", yesterday.toString())
                .addLong("time", System.currentTimeMillis())
                .toJobParameters();

        try {
            logger.info("Daily settlement process. Executing " + yesterday + " settlement.");
            jobLauncher.run(dailySettlementJob, jobParameters);
        } catch (JobExecutionAlreadyRunningException | JobRestartException |
                 JobInstanceAlreadyCompleteException | JobParametersInvalidException e) {
            logger.error("Error processing daily settlement for " + yesterday + ".", e);
        }
    }
}