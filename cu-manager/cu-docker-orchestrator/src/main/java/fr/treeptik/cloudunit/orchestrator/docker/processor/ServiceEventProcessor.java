package fr.treeptik.cloudunit.orchestrator.docker.processor;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.integration.annotation.MessageEndpoint;

import fr.treeptik.cloudunit.domain.resource.ServiceEvent;
import fr.treeptik.cloudunit.orchestrator.core.Container;
import fr.treeptik.cloudunit.orchestrator.core.Image;
import fr.treeptik.cloudunit.orchestrator.docker.OrchestratorChannels;
import fr.treeptik.cloudunit.orchestrator.docker.repository.ContainerRepository;
import fr.treeptik.cloudunit.orchestrator.docker.repository.ImageRepository;
import fr.treeptik.cloudunit.orchestrator.docker.service.DockerService;

@MessageEndpoint
public class ServiceEventProcessor {

	private static final Logger LOGGER = LoggerFactory.getLogger(ServiceEventProcessor.class);

	@Autowired
	private ContainerRepository containerRepository;

	@Autowired
	private ImageRepository imageRepository;

	@Autowired
	private DockerService dockerService;

	@StreamListener(OrchestratorChannels.SERVICES)
	public void onServiceEvent(ServiceEvent event) {
		String containerName = event.getService().getContainerName();
		String imageName = event.getService().getImageName();
		ServiceEvent.Type type = event.getType();
		updateContainerState(containerName, imageName, type);
	}

	private void updateContainerState(String containerName, String imageName, ServiceEvent.Type type) {
		if (type.equals(ServiceEvent.Type.CREATED)) {
			Optional<Image> image = imageRepository.findByName(imageName);
			if (!image.isPresent()) {
				LOGGER.warn("Tried to create a container {} with an unknown image {}", containerName, imageName);
				return;
			}
			dockerService.createContainer(containerName, image.get());
		} else if (type.equals(ServiceEvent.Type.DELETED)) {
			Optional<Container> container = containerRepository.findByName(containerName);
			if (!container.isPresent()) {
				LOGGER.warn("Tried to delete an unknown container {}", containerName);
				return;
			}
			dockerService.deleteContainer(container.get());
		} else if (type.equals(ServiceEvent.Type.STARTED)) {
			Optional<Container> container = containerRepository.findByName(containerName);
			if (!container.isPresent()) {
				LOGGER.warn("Tried to start an unknown container {}", containerName);
				return;
			}
			dockerService.startContainer(container.get());
		} else if (type.equals(ServiceEvent.Type.STOPPED)) {
			Optional<Container> container = containerRepository.findByName(containerName);
			if (!container.isPresent()) {
				LOGGER.warn("Tried to stop an unknown container {}", containerName);
				return;
			}
			dockerService.stopContainer(container.get());
		}
	}

}
