package fr.treeptik.cloudunit.orchestrator.docker.processor;

import fr.treeptik.cloudunit.domain.resource.DeploymentEvent;
import fr.treeptik.cloudunit.domain.resource.ServiceEvent;
import fr.treeptik.cloudunit.orchestrator.core.Container;
import fr.treeptik.cloudunit.orchestrator.core.Image;
import fr.treeptik.cloudunit.orchestrator.docker.OrchestratorChannels;
import fr.treeptik.cloudunit.orchestrator.docker.repository.ContainerRepository;
import fr.treeptik.cloudunit.orchestrator.docker.repository.ImageRepository;
import fr.treeptik.cloudunit.orchestrator.docker.service.DockerService;
import fr.treeptik.cloudunit.orchestrator.docker.service.FileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.integration.annotation.MessageEndpoint;

import java.util.Optional;

@MessageEndpoint
public class DeploymentEventProcessor {

	private static final Logger LOGGER = LoggerFactory.getLogger(DeploymentEventProcessor.class);

	@Autowired
	private ContainerRepository containerRepository;

	@Autowired
	private FileService fileService;

	@StreamListener(OrchestratorChannels.DEPLOYMENTS)
	public void onDeploymentEvent(DeploymentEvent event) {
		String containerName = event.getService().getContainerName();
		String contextPath = event.getDeployment().getContextPath();
		String fileUri = event.getDeployment().getFileUri();
		DeploymentEvent.Type type = event.getType();
		switch (type) {
			case DEPLOYED:
				Optional<Container> container = containerRepository.findByName(containerName);
				if (!container.isPresent()) {
					LOGGER.warn("Tried to deploy an artifact into an unknown container {}", containerName);
					return;
				}
				// deploy the artifact
				fileService.deploy(container.get(), fileUri, contextPath);
				break;
			default:
				LOGGER.error("Type {} unknown", type);
		}
	}

}
