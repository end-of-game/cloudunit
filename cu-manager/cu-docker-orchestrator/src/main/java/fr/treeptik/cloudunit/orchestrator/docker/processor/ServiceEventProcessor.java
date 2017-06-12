package fr.treeptik.cloudunit.orchestrator.docker.processor;

import java.util.Optional;

import fr.treeptik.cloudunit.orchestrator.docker.service.FileService;
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

	@Autowired
	private FileService fileService;

	@StreamListener(OrchestratorChannels.SERVICES)
	public void onServiceEvent(ServiceEvent event) {
		String containerName = event.getService().getContainerName();
		String imageName = event.getService().getImageName();
		ServiceEvent.Type type = event.getType();

		Optional<Container> container = null;
		switch (type) {
			case CREATED:
				Optional<Image> image = imageRepository.findByName(imageName);
				if (!image.isPresent()) {
					LOGGER.error("Tried to create a container {} with an unknown image {}", containerName, imageName);
					return;
				}
				dockerService.createContainer(containerName, image.get());
				break;
			case DELETED:
				container = containerRepository.findByName(containerName);
				if (!container.isPresent()) {
					LOGGER.error("Tried to delete an unknown container {}", containerName);
					return;
				}
				dockerService.deleteContainer(container.get());
				break;
			case STARTED:
				container = containerRepository.findByName(containerName);
				if (!container.isPresent()) {
					LOGGER.error("Tried to start an unknown container {}", containerName);
					return;
				}
				dockerService.startContainer(container.get());
				break;
			case STOPPED:
				container = containerRepository.findByName(containerName);
				if (!container.isPresent()) {
					LOGGER.error("Tried to stop an unknown container {}", containerName);
					return;
				}
				dockerService.stopContainer(container.get());
				break;
			default:
				LOGGER.error("Type {} unknown", type);
		}
	}

}
