package fr.treeptik.cloudunit.config.events;

import org.springframework.context.ApplicationEvent;

import fr.treeptik.cloudunit.model.Application;

/**
 * Created by nicolas on 03/08/2016.
 */
public class ApplicationStartEvent extends ApplicationEvent {

	private static final long serialVersionUID = 1L;

	public ApplicationStartEvent(Application source) {
		super(source);
	}

}
