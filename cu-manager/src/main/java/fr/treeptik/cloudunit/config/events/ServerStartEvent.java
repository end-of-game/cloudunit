package fr.treeptik.cloudunit.config.events;

import fr.treeptik.cloudunit.model.Server;
import org.springframework.context.ApplicationEvent;

/**
 * Created by nicolas on 03/08/2016.
 */
public class ServerStartEvent extends ApplicationEvent {
    
    public ServerStartEvent(Server source) {
        super(source);
    }

}
