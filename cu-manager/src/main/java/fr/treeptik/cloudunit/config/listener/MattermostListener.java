package fr.treeptik.cloudunit.config.listener;

import fr.treeptik.cloudunit.config.MattermostClient;
import fr.treeptik.cloudunit.config.events.ApplicationStartEvent;
import fr.treeptik.cloudunit.model.Application;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component
public class MattermostListener {

    private Logger logger = LoggerFactory.getLogger(ApplicationListener.class);

    @Inject
    private MattermostClient mattermostClient;

    @EventListener
    public void onApplicationStart(ApplicationStartEvent applicationStartEvent) {
        Application application = (Application) applicationStartEvent.getSource();
        mattermostClient.addMessage("cloudunit", "events", application.getDisplayName() + " is starting");
    }

}
