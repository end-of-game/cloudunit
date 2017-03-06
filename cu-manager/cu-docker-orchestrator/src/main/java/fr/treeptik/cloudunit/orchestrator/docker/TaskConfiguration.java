package fr.treeptik.cloudunit.orchestrator.docker;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TaskConfiguration {
    @Bean
    public ThreadFactory threadFactory() {
        return Executors.defaultThreadFactory();
    }
}
