package fr.treeptik.cloudunit.config.listener;

import javax.inject.Inject;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import fr.treeptik.cloudunit.config.events.ApplicationStartEvent;
import fr.treeptik.cloudunit.model.Application;
import fr.treeptik.cloudunit.model.Status;
import fr.treeptik.cloudunit.service.ApplicationService;

/**
 * Created by nicolas on 03/08/2016.
 */
@Component
public class ApplicationListener {

	@Inject
	ApplicationService applicationService;

	@EventListener
	@Async
	public void onServerStart(ApplicationStartEvent applicationStartEvent) {
		Application application = (Application) applicationStartEvent.getSource();
		try {
			int counter = 0;
			boolean started = false;
			do {
				started = applicationService.isStarted(application.getName());
				Thread.sleep(1000);
			} while (counter++ < 30 && !started);
			if (counter <= 30) {
				application.setStatus(Status.START);
			} else {
				application.setStatus(Status.FAIL);
			}
			applicationService.saveInDB(application);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
