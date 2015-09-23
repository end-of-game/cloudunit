package fr.treeptik.cloudunit.service;

import fr.treeptik.cloudunit.exception.CheckException;
import fr.treeptik.cloudunit.exception.ServiceException;
import fr.treeptik.cloudunit.model.Application;
import fr.treeptik.cloudunit.model.Server;

import java.util.List;

public interface ServerService {

	List<Server> findAll() throws ServiceException;

	Server findById(Integer id) throws ServiceException;

	Server remove(String serverName) throws ServiceException;

	Server update(Server server) throws ServiceException;

	Server startServer(Server server) throws ServiceException;

	Server stopServer(Server server) throws ServiceException;

	Server restartServer(Server server) throws ServiceException;

	List<Server> findByApp(Application application) throws ServiceException;

	Server findByName(String serverName) throws ServiceException;

	void checkMaxNumberReach(Application application) throws ServiceException,
			CheckException;

	Server saveInDB(Server server) throws ServiceException;

	List<Server> findAllStatusStopServers() throws ServiceException;

	List<Server> findAllStatusStartServers() throws ServiceException;

	void checkStatus(Server server, String status) throws CheckException;

	boolean checkStatusPENDING(Server server) throws ServiceException;

	Server update(Server server, String memory, String options, String release,
			String location) throws ServiceException;

	Server findByContainerID(String id) throws ServiceException;

	Server confirmSSHDStart(String applicationName, String userLogin)
			throws ServiceException;

	void changeJavaVersion(String applicationName, String javaVersion)
			throws CheckException, ServiceException;

	Server create(Server server, String tag) throws ServiceException,
			CheckException;

	void openPort(String applicationName, String port, String alias,
			boolean isRunning) throws ServiceException;

	void closePort(String applicationName, String port, String alias,
			boolean isRunning) throws ServiceException;
}
