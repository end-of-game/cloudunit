package fr.treeptik.cloudunit.service.impl;

import fr.treeptik.cloudunit.dao.DeploymentDAO;
import fr.treeptik.cloudunit.exception.CheckException;
import fr.treeptik.cloudunit.exception.ServiceException;
import fr.treeptik.cloudunit.model.Application;
import fr.treeptik.cloudunit.model.Deployment;
import fr.treeptik.cloudunit.model.Type;
import fr.treeptik.cloudunit.service.ApplicationService;
import fr.treeptik.cloudunit.service.DeploymentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.persistence.PersistenceException;
import java.util.Date;
import java.util.List;

@Service
public class DeploymentServiceImpl implements DeploymentService {

	@Inject
	private DeploymentDAO deploymentDAO;

	@Inject
	private ApplicationService applicationService;

	@Override
	@Transactional
	public Deployment create(Application application, Type deploymentType)
			throws ServiceException, CheckException {
		try {
			Deployment deployment = new Deployment();
			deployment.setApplication(application);
			deployment.setType(deploymentType);
			deployment.setDate(new Date());
			application = applicationService.findByNameAndUser(application
					.getUser(), application.getName());
			application.setDeploymentStatus(Application.ALREADY_DEPLOYED);
			application = applicationService.saveInDB(application);
			return deploymentDAO.save(deployment);
		} catch (PersistenceException e) {
			throw new ServiceException(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public Deployment find(Deployment deployment) throws ServiceException {
		try {
			return deploymentDAO.findOne(deployment.getId());
		} catch (PersistenceException e) {
			throw new ServiceException(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public List<Deployment> findByApp(Application application)
			throws ServiceException {
		try {
			return deploymentDAO.findAllByApplication(application);
		} catch (PersistenceException e) {
			throw new ServiceException(e.getLocalizedMessage()
					+ application.getName(), e);
		}
	}
}
