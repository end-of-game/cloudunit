package fr.treeptik.cloudunitmonitor.utils;

import java.io.Serializable;

import fr.treeptik.cloudunit.model.Container;
import fr.treeptik.cloudunit.model.Module;
import fr.treeptik.cloudunit.model.Server;
import org.springframework.stereotype.Component;

import fr.treeptik.cloudunitmonitor.docker.model.DockerContainer;


@Component
public class ContainerMapper implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Assign docker's container properties to object handle by cloudunit
	 * (servers, modules)
	 * 
	 * @param dockerContainer
	 * @param container
	 * @return
	 */
	private Container mapDockerContainerToContainer(
			DockerContainer dockerContainer, Container container) {

		container.setContainerID(dockerContainer.getId().substring(0, 12));
		container.setName(dockerContainer.getName());
		container.setMemorySize(dockerContainer.getMemory());
		container.setMemorySize(dockerContainer.getMemorySwap());
		container.setContainerIP(dockerContainer.getIp());

		container.setDockerState(dockerContainer.getState());

		// Set sshPort and delete it from global list port.
		container.setSshPort(dockerContainer.getPorts().get("22/tcp"));
		dockerContainer.getPorts().remove("22/tcp");

		container.setListPorts(dockerContainer.getPorts());

		container.setVolumes(dockerContainer.getVolumes());
		container.setVolumesFrom(dockerContainer.getVolumesFrom());

		return container;

	}

	private Container mapDockerContainerToContainer(
			DockerContainer dockerContainer) {

		Container container = new Container();

		this.mapDockerContainerToContainer(dockerContainer, container);

		return container;

	}

	public Server mapDockerContainerToServer(DockerContainer dockerContainer) {

		Server server = (Server) mapDockerContainerToContainer(dockerContainer);

		return server;

	}

	public Server mapDockerContainerToServer(DockerContainer dockerContainer,
			Server server) {

		mapDockerContainerToContainer(dockerContainer, server);

		return server;

	}

	public Module mapDockerContainerToModule(DockerContainer dockerContainer) {

		return (Module) mapDockerContainerToContainer(dockerContainer);

	}

	public Module mapDockerContainerToModule(DockerContainer dockerContainer,
			Module module) {

		return (Module) mapDockerContainerToContainer(dockerContainer, module);

	}

}
