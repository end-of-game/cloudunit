package fr.treeptik.cloudunit.domain.service;

import fr.treeptik.cloudunit.domain.core.Application;
import fr.treeptik.cloudunit.domain.core.Deployment;
import fr.treeptik.cloudunit.domain.core.Service;

public interface DeploymentListener {

	void onDeployment(Application application,
					  Service service,
					  Deployment deployment);

}
