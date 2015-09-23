package fr.treeptik.cloudunit.service;

import fr.treeptik.cloudunit.exception.CheckException;
import fr.treeptik.cloudunit.exception.DockerJSONException;
import fr.treeptik.cloudunit.exception.ProviderException;
import fr.treeptik.cloudunit.exception.ServiceException;

public interface DockerService {

	public void checkAllContainersStatus() throws ServiceException, CheckException;

	void checkAllApplicationContainersStatus() throws ServiceException,
			CheckException, DockerJSONException, ProviderException;

	public String checkDockerInfos() throws ServiceException;
}
