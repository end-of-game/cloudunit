package fr.treeptik.cloudunit.service.impl;

import fr.treeptik.cloudunit.dao.EnvironmentDAO;
import fr.treeptik.cloudunit.dto.EnvironmentVariableRequest;
import fr.treeptik.cloudunit.exception.CheckException;
import fr.treeptik.cloudunit.exception.ServiceException;
import fr.treeptik.cloudunit.model.Application;
import fr.treeptik.cloudunit.model.Environment;
import fr.treeptik.cloudunit.model.User;
import fr.treeptik.cloudunit.service.ApplicationService;
import fr.treeptik.cloudunit.service.EnvironmentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EnvironmentServiceImpl implements EnvironmentService {

	@Inject
	private EnvironmentDAO environmentDAO;

	@Inject
	private ApplicationService applicationService;

	@Transactional
	public EnvironmentVariableRequest save(User user, EnvironmentVariableRequest environmentVariableRequest,
			String applicationName, String containerName) throws ServiceException, CheckException {
		if (environmentVariableRequest.getKey() == null || environmentVariableRequest.getKey().isEmpty())
			throw new CheckException("This key is not consistent !");

		if (!environmentVariableRequest.getKey().matches("^[-a-zA-Z0-9_]*$"))
			throw new CheckException("This key is not consistent : " + environmentVariableRequest.getKey());

		List<Environment> environmentList = environmentDAO.findByContainer(containerName);

		Optional<Environment> value = environmentList.stream()
				.filter(v -> v.getKeyEnv().equals(environmentVariableRequest.getKey())).findFirst();

		if (value.isPresent())
			throw new CheckException("This key already exists");

		Application application = applicationService.findByNameAndUser(user, applicationName);
		Environment environment = new Environment();

		environment.setApplication(application);
		environment.setContainerName(containerName);
		environment.setKeyEnv(environmentVariableRequest.getKey());
		environment.setValueEnv(environmentVariableRequest.getValue());

		environmentDAO.save(environment);

		List<EnvironmentVariableRequest> environmentVariableRequestList = loadEnvironnmentsByContainer(containerName);

		Optional<EnvironmentVariableRequest> value2 = environmentVariableRequestList.stream()
				.filter(v -> v.getKey().equals(environment.getKeyEnv())).findFirst();

		return value2.get();
	}

	@Override
	public EnvironmentVariableRequest loadEnvironnment(int id) throws ServiceException, CheckException {

		Environment environment = environmentDAO.findById(id);

		if (environment.equals(null))
			throw new CheckException("Environment variable doesn't exist");

		EnvironmentVariableRequest environmentVariableRequest = new EnvironmentVariableRequest();
		environmentVariableRequest.setId(environment.getId());
		environmentVariableRequest.setKey(environment.getKeyEnv());
		environmentVariableRequest.setValue(environment.getValueEnv());

		return environmentVariableRequest;
	}

	@Override
	public List<EnvironmentVariableRequest> loadEnvironnmentsByContainer(String containerName) throws ServiceException {
		List<Environment> environmentList = environmentDAO.findByContainer(containerName);
		List<EnvironmentVariableRequest> environmentVariableRequestList = environmentList.stream()
				.map(v -> v.mapToRequest()).collect(Collectors.toList());

		return environmentVariableRequestList;
	}

	@Override
	@Transactional
	public void delete(int id) throws ServiceException, CheckException {
		Environment environment = environmentDAO.findById(id);

		if (environment.equals(null)) {
			throw new CheckException("Environment variable doesn't exist");
		}

		environmentDAO.delete(id);
	}

	public EnvironmentVariableRequest update(User user, EnvironmentVariableRequest environmentVariableRequest,
			String applicationName, String containerName, Integer id) throws ServiceException, CheckException {
		if (environmentVariableRequest.getKey() == null || environmentVariableRequest.getKey().isEmpty())
			throw new CheckException("This key is not consistent !");

		if (!environmentVariableRequest.getKey().matches("^[-a-zA-Z0-9_]*$"))
			throw new CheckException("This key is not consistent : " + environmentVariableRequest.getKey());

		Environment environment = environmentDAO.findById(id);
		List<Environment> environmentList = environmentDAO.findByContainer(containerName);

		Optional<Environment> value = environmentList.stream()
				.filter(v -> v.getKeyEnv().equals(environmentVariableRequest.getKey())
						&& !v.getKeyEnv().equals(environment.getKeyEnv()))
				.findFirst();

		if (value.isPresent())
			throw new CheckException("This key already exists");

		if (environment.equals(null))
			throw new CheckException("Environment variable doesn't exist");

		environment.setKeyEnv(environmentVariableRequest.getKey());
		environment.setValueEnv(environmentVariableRequest.getValue());

		environmentDAO.save(environment);

		EnvironmentVariableRequest returnEnv = new EnvironmentVariableRequest();
		returnEnv.setId(environment.getId());
		returnEnv.setKey(environment.getKeyEnv());
		returnEnv.setValue(environment.getValueEnv());

		return returnEnv;
	}
}
