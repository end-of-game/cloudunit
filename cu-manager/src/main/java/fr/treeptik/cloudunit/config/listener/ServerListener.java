package fr.treeptik.cloudunit.config.listener;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import fr.treeptik.cloudunit.config.events.ServerStartEvent;
import fr.treeptik.cloudunit.config.events.ServerStopEvent;
import fr.treeptik.cloudunit.enums.RemoteExecAction;
import fr.treeptik.cloudunit.model.Server;
import fr.treeptik.cloudunit.model.Status;
import fr.treeptik.cloudunit.service.DockerService;
import fr.treeptik.cloudunit.service.ServerService;

/**
 * Created by nicolas on 03/08/2016.
 */
@Component
public class ServerListener {

	private Logger logger = LoggerFactory.getLogger(ApplicationListener.class);

	@Inject
	DockerService dockerService;

	@Inject
	ServerService serverService;

	@EventListener
	@Async
	public void onServerStart(ServerStartEvent serverStartEvent) {
		Server server = (Server) serverStartEvent.getSource();
		try {
			String containerId = server.getContainerID();
			int counter = 0;
			boolean notStarted = true;
			do {
				String command = RemoteExecAction.CHECK_RUNNING.getCommand();
				String exec = dockerService.execCommand(containerId, command);
				if ("0".equalsIgnoreCase(exec.trim())) {
					notStarted = false;
					break;
				}
				Thread.sleep(1000);
			} while (counter++ < 30 && notStarted);
			if (counter <= 30) {
				server.setStatus(Status.START);
			} else {
				server.setStatus(Status.FAIL);
			}
			logger.info("Server status : " + server.getStatus());
			serverService.update(server);
		} catch (Exception e) {
			logger.error(server.toString(), e);
			e.printStackTrace();
		}
	}

	@EventListener
	@Async
	public void onServerStop(ServerStopEvent serverStopEvent) {
		Server server = (Server) serverStopEvent.getSource();
		try {
			int counter = 0;
			boolean isStopped = false;
			do {
				isStopped = dockerService.isStoppedGracefully(server.getContainerID());
				Thread.sleep(1000);
			} while (counter++ < 30 && !isStopped);
			if (counter <= 30) {
				server.setStatus(Status.STOP);
			} else {
				server.setStatus(Status.FAIL);
			}
			logger.info("Server status : " + server.getStatus());
			serverService.update(server);
		} catch (Exception e) {
			logger.error(server.getContainerID(), e);
			e.printStackTrace();
		}
	}

}
