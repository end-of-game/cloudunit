package fr.treeptik.cloudunit.domain.publisher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import fr.treeptik.cloudunit.domain.DomainChannels;
import fr.treeptik.cloudunit.domain.core.Service;
import fr.treeptik.cloudunit.domain.resource.ServiceEvent;
import fr.treeptik.cloudunit.domain.resource.ServiceResource;
import fr.treeptik.cloudunit.domain.service.ServiceListener;

@Component
public class ServiceEventPublisher implements ServiceListener {

	@Autowired
	@Qualifier(DomainChannels.SERVICES)
	private SubscribableChannel services;
	private void onServiceEvent(ServiceEvent.Type type, Service service) {
		ServiceEvent event = new ServiceEvent(type, new ServiceResource(service));
		services.send(MessageBuilder.withPayload(event).build());
	}

	@Override
	public void onServiceCreated(Service service) {
		onServiceEvent(ServiceEvent.Type.CREATED, service);
	}

	@Override
	public void onServiceDeleted(Service service) {
		onServiceEvent(ServiceEvent.Type.DELETED, service);
	}

	@Override
	public void onServiceStarted(Service service) {
		onServiceEvent(ServiceEvent.Type.STARTED, service);
	}

	@Override
	public void onServiceStopped(Service service) {
		onServiceEvent(ServiceEvent.Type.STOPPED, service);
	}

}
