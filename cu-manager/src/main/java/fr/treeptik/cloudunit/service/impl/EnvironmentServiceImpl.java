package fr.treeptik.cloudunit.service.impl;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.treeptik.cloudunit.dao.EnvironmentDAO;
import fr.treeptik.cloudunit.exception.CheckException;
import fr.treeptik.cloudunit.exception.ServiceException;
import fr.treeptik.cloudunit.model.Application;
import fr.treeptik.cloudunit.model.EnvironmentVariable;
import fr.treeptik.cloudunit.model.User;
import fr.treeptik.cloudunit.service.ApplicationService;
import fr.treeptik.cloudunit.service.EnvironmentService;

@Service
public class EnvironmentServiceImpl implements EnvironmentService {

	@Inject
	private EnvironmentDAO environmentDAO;

	@Inject
	private ApplicationService applicationService;

	@Transactional
	public EnvironmentVariable save(User user, EnvironmentVariable environmentVariableRequest, String applicationName,
			String containerName) throws ServiceException, CheckException {
		if (environmentVariableRequest.getKeyEnv() == null || environmentVariableRequest.getKeyEnv().isEmpty())
			throw new CheckException("This key is not consistent !");

		if (!environmentVariableRequest.getKeyEnv().matches("^[-a-zA-Z0-9_]*$"))
			throw new CheckException("This key is not consistent : " + environmentVariableRequest.getKeyEnv());

		List<EnvironmentVariable> environmentList = environmentDAO.findByContainer(containerName);

		Optional<EnvironmentVariable> value = environmentList.stream()
				.filter(v -> v.getKeyEnv().equals(environmentVariableRequest.getKeyEnv())).findFirst();

		if (value.isPresent())
			throw new CheckException("This key already exists");

		Application application = applicationService.findByNameAndUser(user, applicationName);
		EnvironmentVariable environment = new EnvironmentVariable();

		environment.setApplication(application);
		environment.setContainerName(containerName);
		environment.setKeyEnv(environmentVariableRequest.getKeyEnv());
		environment.setValueEnv(environmentVariableRequest.getValueEnv());

		environmentDAO.save(environment);

		List<EnvironmentVariable> environmentVariableRequestList = loadEnvironnmentsByContainer(containerName);

		Optional<EnvironmentVariable> value2 = environmentVariableRequestList.stream()
				.filter(v -> v.getKeyEnv().equals(environment.getKeyEnv())).findFirst();

		return value2.get();
	}

	@Override
	public EnvironmentVariable loadEnvironnment(int id) throws ServiceException, CheckException {

		EnvironmentVariable environment = environmentDAO.findById(id);

		if (environment.equals(null))
			throw new CheckException("Environment variable doesn't exist");

		return environment;
	}

	@Override
	public List<EnvironmentVariable> loadEnvironnmentsByContainer(String containerName) throws ServiceException {
		List<EnvironmentVariable> environmentList = environmentDAO.findByContainer(containerName);
		return environmentList;
	}

	@Override
	@Transactional
	public void delete(int id) throws ServiceException, CheckException {
		EnvironmentVariable environment = environmentDAO.findById(id);

		if (environment.equals(null)) {
			throw new CheckException("Environment variable doesn't exist");
		}

		environmentDAO.delete(id);
	}

	public EnvironmentVariable update(User user, EnvironmentVariable environmentVariableRequest, String applicationName,
			String containerName, Integer id) throws ServiceException, CheckException {
		if (environmentVariableRequest.getKeyEnv() == null || environmentVariableRequest.getKeyEnv().isEmpty())
			throw new CheckException("This key is not consistent !");

		if (!environmentVariableRequest.getKeyEnv().matches("^[-a-zA-Z0-9_]*$"))
			throw new CheckException("This key is not consistent : " + environmentVariableRequest.getKeyEnv());

		final EnvironmentVariable environment = environmentDAO.findById(id);
		List<EnvironmentVariable> environmentList = environmentDAO.findByContainer(containerName);

		Optional<EnvironmentVariable> value = environmentList.stream()
				.filter(v -> v.getKeyEnv().equals(environmentVariableRequest.getKeyEnv())
						&& !v.getKeyEnv().equals(environment.getKeyEnv()))
				.findFirst();

		if (value.isPresent())
			throw new CheckException("This key already exists");

		if (environment.equals(null))
			throw new CheckException("Environment variable doesn't exist");

		return environmentDAO.save(environment);

	}
}
