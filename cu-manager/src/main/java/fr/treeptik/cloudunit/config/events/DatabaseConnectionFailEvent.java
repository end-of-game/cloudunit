package fr.treeptik.cloudunit.config.events;

import org.springframework.context.ApplicationEvent;

/**
 * Created by guillaume on 08/10/16.
 */
public class DatabaseConnectionFailEvent extends ApplicationEvent {

    private static final long serialVersionUID = 1L;

    public DatabaseConnectionFailEvent(Object source) {
        super(source);
    }
}
