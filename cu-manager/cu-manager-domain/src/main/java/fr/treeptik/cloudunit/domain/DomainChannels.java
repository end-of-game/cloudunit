package fr.treeptik.cloudunit.domain;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.SubscribableChannel;

public interface DomainChannels {
	String APPLICATIONS = "applications";
	String SERVICES = "services";
	String CONTAINERS = "containers";
	String IMAGES = "images";

	@Output(APPLICATIONS)
	SubscribableChannel applications();

	@Output(SERVICES)
	SubscribableChannel services();

	@Input(CONTAINERS)
	SubscribableChannel containers();

	@Input(IMAGES)
	SubscribableChannel images();
}
