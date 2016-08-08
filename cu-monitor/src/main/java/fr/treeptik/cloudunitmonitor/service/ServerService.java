package fr.treeptik.cloudunitmonitor.service;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.PersistenceException;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.treeptik.cloudunit.model.Application;
import fr.treeptik.cloudunit.model.PortToOpen;
import fr.treeptik.cloudunit.model.Server;
import fr.treeptik.cloudunitmonitor.dao.ServerDAO;
import fr.treeptik.cloudunitmonitor.docker.model.DockerContainer;
import fr.treeptik.cloudunitmonitor.exception.DockerJSONException;
import fr.treeptik.cloudunitmonitor.exception.ServiceException;
import fr.treeptik.cloudunitmonitor.utils.ContainerMapper;
import fr.treeptik.cloudunitmonitor.utils.HipacheRedisUtils;

@Service
public class ServerService {

	@Inject
	private ServerDAO serverDAO;

	@Inject
	private HipacheRedisUtils hipacheRedisUtils;

	@Inject
	private ContainerMapper containerMapper;

	public List<Server> findAll() throws ServiceException {
		try {
			return serverDAO.findAll();
		} catch (DataAccessException e) {
			throw new ServiceException("error find all servers", e);
		}
	}

	public void updatePortAlias(PortToOpen portToOpen, Application application) {
		if ("web".equalsIgnoreCase(portToOpen.getNature())) {
			hipacheRedisUtils.updatePortAlias(application.getServer().getContainerIP(), portToOpen.getPort(),
					portToOpen.getAlias().substring(portToOpen.getAlias().lastIndexOf("//") + 2));
		}
	}

	@Transactional
	public Server startServer(Server server) throws ServiceException {

		try {
			Application application = server.getApplication();

			DockerContainer dockerContainer = new DockerContainer();
			dockerContainer.setName(server.getName());
			dockerContainer.setImage(server.getImage().getName());

			DockerContainer.start(dockerContainer, application.getManagerIp());
			dockerContainer = DockerContainer.findOne(dockerContainer, application.getManagerIp());

			server = containerMapper.mapDockerContainerToServer(dockerContainer, server);

			server.setStartDate(new Date());

			server = serverDAO.saveAndFlush(server);
			server = serverDAO.findOne(server.getId());

			hipacheRedisUtils.updateServerAddress(server.getApplication(), server.getContainerIP(),
					server.getServerAction().getServerPort(), server.getServerAction().getServerManagerPort());
			final Application app = server.getApplication();
			app.getPortsToOpen().stream().forEach(p -> updatePortAlias(p, app));

		} catch (PersistenceException e) {

			throw new ServiceException("Error database :  " + e.getLocalizedMessage(), e);
		} catch (DockerJSONException e) {

			throw new ServiceException("Error docker :  " + e.getLocalizedMessage(), e);
		}
		return server;
	}
}
