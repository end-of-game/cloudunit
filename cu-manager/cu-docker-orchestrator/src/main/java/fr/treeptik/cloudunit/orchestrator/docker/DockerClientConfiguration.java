package fr.treeptik.cloudunit.orchestrator.docker;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties
public class DockerClientConfiguration {
    @Bean
    @ConfigurationProperties(prefix = "docker.client")
    public DockerClientFactory dockerClient() {
        return new DockerClientFactory();
    }
}
