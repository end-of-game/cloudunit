package fr.treeptik.cloudunit.config.events;

import org.springframework.context.ApplicationEvent;

import fr.treeptik.cloudunit.dto.Hook;

public class HookEvent extends ApplicationEvent {
    private static final long serialVersionUID = 1L;

    public HookEvent(Hook source) {
        super(source);
    }

}
