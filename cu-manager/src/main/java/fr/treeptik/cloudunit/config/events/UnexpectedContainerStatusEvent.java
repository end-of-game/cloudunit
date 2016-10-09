package fr.treeptik.cloudunit.config.events;

import org.springframework.context.ApplicationEvent;

/**
 * Created by guillaume on 08/10/16.
 */
public class UnexpectedContainerStatusEvent extends ApplicationEvent {
    /**
     * Create a new ApplicationEvent.
     *
     * @param source the object on which the event initially occurred (never {@code null})
     */
    public UnexpectedContainerStatusEvent(Object source) {
        super(source);
    }
}
