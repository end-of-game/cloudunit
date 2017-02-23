package fr.treeptik.cloudunit.domain.test;

import java.util.Random;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestConfiguration {
    private static final String TEST_APPLICATION_NAME_FORMAT = "app%s";
    private static final int MAX_TEST_APPLICATION_NAME = 100000;

    @Bean("testApplicationName")
    public String testApplicationName() {
        return String.format(TEST_APPLICATION_NAME_FORMAT, new Random().nextInt(MAX_TEST_APPLICATION_NAME));
    }
}
