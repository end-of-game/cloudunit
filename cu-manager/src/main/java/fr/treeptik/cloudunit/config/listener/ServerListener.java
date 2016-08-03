package fr.treeptik.cloudunit.config.listener;

import fr.treeptik.cloudunit.config.events.ServerStartEvent;
import fr.treeptik.cloudunit.config.events.ServerStopEvent;
import fr.treeptik.cloudunit.enums.RemoteExecAction;
import fr.treeptik.cloudunit.model.Server;
import fr.treeptik.cloudunit.model.Status;
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
public class ServerListener  {

    @Inject
    DockerService dockerService;

    @Inject
    ServerService serverService;

    @EventListener
    @Async
    public void onServerStart(ServerStartEvent serverStartEvent)  {
        try {
            Server server = (Server) serverStartEvent.getSource();
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
            serverService.update(server);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @EventListener
    @Async
    public void onServerStop(ServerStopEvent serverStopEvent) {
        Server server = (Server)serverStopEvent.getSource();
        System.out.println(server.getContainerID());
    }

}
