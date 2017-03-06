package fr.treeptik.cloudunit.domain;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TaskConfiguration {
    @Bean
    public ThreadFactory threadFactory() {
        return Executors.defaultThreadFactory();
    }
    
    @Bean
    public ScheduledExecutorService executor(ThreadFactory threadFactory) {
        return Executors.newSingleThreadScheduledExecutor(threadFactory);
    }
}
