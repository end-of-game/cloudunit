/*
 * LICENCE : CloudUnit is available under the Gnu Public License GPL V3 : https://www.gnu.org/licenses/gpl.txt
 *     but CloudUnit is licensed too under a standard commercial license.
 *     Please contact our sales team if you would like to discuss the specifics of our Enterprise license.
 *     If you are not sure whether the GPL is right for you,
 *     you can always test our software under the GPL and inspect the source code before you contact us
 *     about purchasing a commercial license.
 *
 *     LEGAL TERMS : "CloudUnit" is a registered trademark of Treeptik and can't be used to endorse
 *     or promote products derived from this project without prior written permission from Treeptik.
 *     Products or services derived from this software may not be called "CloudUnit"
 *     nor may "Treeptik" or similar confusing terms appear in their names without prior written permission.
 *     For any questions, contact us : contact@treeptik.fr
 */

package fr.treeptik.cloudunit.utils;

import fr.treeptik.cloudunit.docker.model.DockerContainer;
import fr.treeptik.cloudunit.model.Container;
import fr.treeptik.cloudunit.model.Module;
import fr.treeptik.cloudunit.model.Server;
import org.springframework.stereotype.Component;

import java.io.Serializable;

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
		container.setContainerFullID(dockerContainer.getId());
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
