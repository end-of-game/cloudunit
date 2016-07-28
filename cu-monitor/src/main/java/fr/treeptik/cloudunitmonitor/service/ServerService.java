package fr.treeptik.cloudunitmonitor.service;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.PersistenceException;

import fr.treeptik.cloudunit.model.Application;
import fr.treeptik.cloudunit.model.Server;
import fr.treeptik.cloudunitmonitor.utils.HipacheRedisUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.treeptik.cloudunitmonitor.conf.ApplicationEntryPoint;
import fr.treeptik.cloudunitmonitor.dao.ServerDAO;
import fr.treeptik.cloudunitmonitor.docker.model.DockerContainer;
import fr.treeptik.cloudunitmonitor.exception.DockerJSONException;
import fr.treeptik.cloudunitmonitor.exception.ServiceException;
import fr.treeptik.cloudunitmonitor.utils.ContainerMapper;

@Service
public class ServerService {

	private Logger logger = LoggerFactory.getLogger(ServerService.class);

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

	@Transactional
	public Server startServer(Server server) throws ServiceException {

		String redisIp = ApplicationEntryPoint.IP_REDIS;

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

			hipacheRedisUtils.updateServerAddress(server.getApplication(),
					server.getContainerIP(),
					server.getServerAction().getServerPort(),
					server.getServerAction().getServerManagerPort());

		} catch (PersistenceException e) {

			throw new ServiceException("Error database :  " + e.getLocalizedMessage(), e);
		} catch (DockerJSONException e) {

			throw new ServiceException("Error docker :  " + e.getLocalizedMessage(), e);
		}
		return server;
	}
}
