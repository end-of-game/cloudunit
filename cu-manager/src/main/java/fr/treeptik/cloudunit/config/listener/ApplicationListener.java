package fr.treeptik.cloudunit.config.listener;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import fr.treeptik.cloudunit.config.events.ApplicationFailEvent;
import fr.treeptik.cloudunit.config.events.ApplicationPendingEvent;
import fr.treeptik.cloudunit.config.events.ApplicationStartEvent;
import fr.treeptik.cloudunit.config.events.ApplicationStopEvent;
import fr.treeptik.cloudunit.exception.ServiceException;
import fr.treeptik.cloudunit.model.Application;
import fr.treeptik.cloudunit.model.Status;
import fr.treeptik.cloudunit.service.ApplicationService;

/**
 * Created by nicolas on 03/08/2016.
 */
@Component
public class ApplicationListener {

	private Logger logger = LoggerFactory.getLogger(ApplicationListener.class);

	@Inject
	private ApplicationService applicationService;

	@EventListener
	public void onApplicationStart(ApplicationStartEvent applicationStartEvent) {
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
			logger.info("Application status : " + application.getStatus());
			applicationService.saveInDB(application);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@EventListener
	public void onApplicationStop(ApplicationStopEvent applicationStopEvent) {
		Application application = (Application) applicationStopEvent.getSource();
		try {
			int counter = 0;
			boolean started = false;
			do {
				started = applicationService.isStopped(application.getName());
				Thread.sleep(1000);
			} while (counter++ < 30 && !started);
			if (counter <= 30) {
				application.setStatus(Status.STOP);
			} else {
				application.setStatus(Status.FAIL);
			}
			logger.info("Application status : " + application.getStatus());
			applicationService.saveInDB(application);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

    @EventListener
    @Async
    public void setApplicationPending(ApplicationPendingEvent applicationPendingEvent) {
        Application application = (Application) applicationPendingEvent.getSource();
        application.setStatus(Status.PENDING);
        try {
            applicationService.saveInDB(application);
        } catch (ServiceException e) {
            e.printStackTrace();
        }
    }

    @EventListener
    @Async
    public void setApplicationFail(ApplicationFailEvent applicationFailEvent) {
        Application application = (Application) applicationFailEvent.getSource();
        application.setStatus(Status.FAIL);
        try {
            applicationService.saveInDB(application);
        } catch (ServiceException e) {
            e.printStackTrace();
        }
    }

}
