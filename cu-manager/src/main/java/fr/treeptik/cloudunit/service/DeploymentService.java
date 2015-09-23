package fr.treeptik.cloudunit.service;

import fr.treeptik.cloudunit.exception.CheckException;
import fr.treeptik.cloudunit.exception.ServiceException;
import fr.treeptik.cloudunit.model.Application;
import fr.treeptik.cloudunit.model.Deployment;
import fr.treeptik.cloudunit.model.Type;

import java.util.List;

public interface DeploymentService {

	Deployment find(Deployment deployment) throws ServiceException;

	List<Deployment> findByApp(Application application) throws ServiceException;

	Deployment create(Application application, Type deploymentType)
			throws ServiceException, CheckException;

}
