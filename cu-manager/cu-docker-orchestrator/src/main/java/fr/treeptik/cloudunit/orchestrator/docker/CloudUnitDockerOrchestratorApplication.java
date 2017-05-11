package fr.treeptik.cloudunit.orchestrator.docker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.hateoas.config.EnableHypermediaSupport.HypermediaType;

@SpringBootApplication
@EnableHypermediaSupport(type = HypermediaType.HAL)
@EnableBinding(OrchestratorChannels.class)
public class CloudUnitDockerOrchestratorApplication {
    public static void main(String[] args) {
        SpringApplication.run(CloudUnitDockerOrchestratorApplication.class, args);
    }
}
