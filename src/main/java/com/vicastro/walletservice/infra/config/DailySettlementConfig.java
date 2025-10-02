package com.vicastro.walletservice.infra.config;

import com.vicastro.walletservice.infra.repository.jpa.WalletBalanceJpaRepository;
import com.vicastro.walletservice.infra.repository.jpa.entity.WalletBalanceEntity;
import com.vicastro.walletservice.infra.repository.jpa.entity.WalletEntity;
import com.vicastro.walletservice.infra.service.scheduler.DailySettlementProcessor;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class DailySettlementConfig {

    @Bean
    public JpaPagingItemReader<WalletEntity> walletReader(EntityManagerFactory entityManagerFactory) {
        return new JpaPagingItemReaderBuilder<WalletEntity>()
                .name("walletReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("SELECT w FROM WalletEntity w")
                .pageSize(500)
                .build();
    }

    @Bean
    public RepositoryItemWriter<WalletBalanceEntity> walletBalanceWriter(WalletBalanceJpaRepository repository) {
        return new RepositoryItemWriterBuilder<WalletBalanceEntity>()
                .repository(repository)
                .methodName("save")
                .build();
    }

    @Bean
    public Step settlementStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            JpaPagingItemReader<WalletEntity> walletReader,
            DailySettlementProcessor processor,
            RepositoryItemWriter<WalletBalanceEntity> walletBalanceWriter,
            TaskExecutor virtualThreadTaskExecutor) {

        return new StepBuilder("settlementStep", jobRepository)
                .<WalletEntity, WalletBalanceEntity>chunk(100, transactionManager)
                .reader(walletReader)
                .processor(processor)
                .writer(walletBalanceWriter)
                .taskExecutor(virtualThreadTaskExecutor)
                .build();
    }

    @Bean
    public Job dailySettlementJob(JobRepository jobRepository, Step settlementStep) {
        return new JobBuilder("dailySettlementJob", jobRepository)
                .start(settlementStep)
                .build();
    }
}

