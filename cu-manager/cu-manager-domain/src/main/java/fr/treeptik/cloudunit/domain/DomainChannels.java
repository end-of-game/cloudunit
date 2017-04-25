package fr.treeptik.cloudunit.domain;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.SubscribableChannel;

public interface DomainChannels {
    String APPLICATIONS = "applications";
    String CONTAINERS = "containers";
    String IMAGES = "images";
    
    @Output(APPLICATIONS)
    SubscribableChannel applications();
    
    @Input(CONTAINERS)
    SubscribableChannel containers();
    
    @Input(IMAGES)
    SubscribableChannel images();
}
