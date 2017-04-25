package fr.treeptik.cloudunit.orchestrator.docker.publisher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import fr.treeptik.cloudunit.orchestrator.core.Container;
import fr.treeptik.cloudunit.orchestrator.docker.OrchestratorChannels;
import fr.treeptik.cloudunit.orchestrator.docker.service.DockerServiceListener;
import fr.treeptik.cloudunit.orchestrator.resource.ContainerEvent;
import fr.treeptik.cloudunit.orchestrator.resource.ContainerResource;

@Component
public class ContainerEventPublisher implements DockerServiceListener {
    @Autowired
    @Qualifier(OrchestratorChannels.CONTAINERS)
    private SubscribableChannel containers;

    private void onContainerEvent(ContainerEvent.Type type, Container container) {
        ContainerEvent event = new ContainerEvent(type, new ContainerResource(container));
        containers.send(MessageBuilder.withPayload(event).build());
    }

    @Override
    public void onContainerCreated(Container container) {
        onContainerEvent(ContainerEvent.Type.CREATED, container);
    }

    @Override
    public void onContainerChanged(Container container) {
        onContainerEvent(ContainerEvent.Type.CHANGED, container);
    }

    @Override
    public void onContainerDeleted(Container container) {
        onContainerEvent(ContainerEvent.Type.DELETED, container);
    }
}
