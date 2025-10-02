package com.vicastro.walletservice.infra.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;

import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

@Configuration
public class BatchConfig {

    final int CONCURRENCY_LIMIT = 50;

    @Bean
    public TaskExecutor virtualThreadTaskExecutor() {
        final Semaphore semaphore = new Semaphore(CONCURRENCY_LIMIT);
        return task -> {
            try {
                semaphore.acquire();
                Executors.newVirtualThreadPerTaskExecutor().execute(() -> {
                    try {
                        task.run();
                    } finally {
                        semaphore.release();
                    }
                });
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Fail acquiring VT permission", e);
            }
        };
    }
}
