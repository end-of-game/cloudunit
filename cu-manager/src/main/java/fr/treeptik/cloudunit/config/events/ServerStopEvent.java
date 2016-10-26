package fr.treeptik.cloudunit.config.events;

import fr.treeptik.cloudunit.model.Server;
import org.springframework.context.ApplicationEvent;

/**
 * Created by nicolas on 03/08/2016.
 */
public class ServerStopEvent extends ApplicationEvent {

	private static final long serialVersionUID = 1L;

	public ServerStopEvent(Server source) {
		super(source);
	}

}
