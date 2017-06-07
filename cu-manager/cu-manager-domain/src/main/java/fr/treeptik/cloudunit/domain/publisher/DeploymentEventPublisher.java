package fr.treeptik.cloudunit.domain.publisher;

import fr.treeptik.cloudunit.domain.DomainChannels;
import fr.treeptik.cloudunit.domain.core.Application;
import fr.treeptik.cloudunit.domain.core.Deployment;
import fr.treeptik.cloudunit.domain.core.Service;
import fr.treeptik.cloudunit.domain.resource.ApplicationResource;
import fr.treeptik.cloudunit.domain.resource.DeploymentEvent;
import fr.treeptik.cloudunit.domain.resource.DeploymentResource;
import fr.treeptik.cloudunit.domain.resource.ServiceResource;
import fr.treeptik.cloudunit.domain.service.DeploymentListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
public class DeploymentEventPublisher implements DeploymentListener {

	@Autowired
	@Qualifier(DomainChannels.DEPLOYMENTS)
	private SubscribableChannel deployments;
	private void onDeploymentEvent(DeploymentEvent.Type type, Application application, Service service, Deployment deployment) {
		DeploymentEvent event = new DeploymentEvent(type,
				new ApplicationResource(application),
				new ServiceResource(service),
				new DeploymentResource(deployment));
		deployments.send(MessageBuilder.withPayload(event).build());
	}

	@Override
	public void onDeployment(Application application, Service service, Deployment deployment) {
		onDeploymentEvent(DeploymentEvent.Type.DEPLOYED, application, service, deployment);
	}

}
