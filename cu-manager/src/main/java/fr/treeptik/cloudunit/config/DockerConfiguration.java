package fr.treeptik.cloudunit.config;

import fr.treeptik.cloudunit.service.DockerService;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

/**
 * Created by nicolas on 21/02/2017.
 */
@Component
public class DockerConfiguration {

    @Inject
    private DockerService dockerService;

    private Boolean isAgentPresent;

    @PostConstruct
    public void checkEnvironmentDocker() {
        isAgentPresent = dockerService.exists("cu-monitoring-agents");
    }

    public Boolean isAgentPresent() {
        return isAgentPresent;
    }
}
