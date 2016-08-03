package fr.treeptik.cloudunit.config.listener;

import fr.treeptik.cloudunit.config.events.ApplicationStartEvent;
import fr.treeptik.cloudunit.config.events.ServerStartEvent;
import fr.treeptik.cloudunit.config.events.ServerStopEvent;
import fr.treeptik.cloudunit.enums.RemoteExecAction;
import fr.treeptik.cloudunit.model.Application;
import fr.treeptik.cloudunit.model.Server;
import fr.treeptik.cloudunit.model.Status;
import fr.treeptik.cloudunit.service.ApplicationService;
import fr.treeptik.cloudunit.service.DockerService;
import fr.treeptik.cloudunit.service.ServerService;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

/**
 * Created by nicolas on 03/08/2016.
 */
@Component
public class ApplicationListener {

    @Inject
    ApplicationService applicationService;

    @EventListener
    @Async
    public void onServerStart(ApplicationStartEvent applicationStartEvent)  {
        Application application = (Application) applicationStartEvent.getSource();
        try {
            int counter = 0;
            boolean started = false;
            do {
                started = applicationService.isStarted(application.getId());
                Thread.sleep(1000);
            } while (counter++ < 30 && !started);
            if (counter <= 30) {
                application.setStatus(Status.START);
            } else {
                application.setStatus(Status.FAIL);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
