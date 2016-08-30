package fr.treeptik.cloudunit.config.events;

import org.springframework.context.ApplicationEvent;

import fr.treeptik.cloudunit.model.Module;

/**
 * Created by nicolas on 03/08/2016.
 */
public class ModuleStopEvent extends ApplicationEvent {

	private static final long serialVersionUID = 1L;

	public ModuleStopEvent(Module source) {
		super(source);
	}

}
