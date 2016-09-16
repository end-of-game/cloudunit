package fr.treeptik.cloudunit.config.listener;

import javax.inject.Inject;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import fr.treeptik.cloudunit.config.events.HookEvent;
import fr.treeptik.cloudunit.dto.Hook;
import fr.treeptik.cloudunit.enums.RemoteExecAction;
import fr.treeptik.cloudunit.service.DockerService;
import fr.treeptik.cloudunit.service.HookService;

@Component
public class HookListener {

    @Inject
    private HookService hookService;

    @Inject
    private DockerService dockerService;

    @EventListener
    @Async
    public void onCall(HookEvent hookEvent) throws InterruptedException {
        int counter = 0;
        boolean started = false;
        Hook hook = (Hook) hookEvent.getSource();
        do {
            String command = RemoteExecAction.CHECK_RUNNING.getCommand();
            String exec = dockerService.execCommand(hook.getContainerName(), command);
            exec = exec.replaceAll(System.getProperty("line.separator"), "");
            if ("0".equalsIgnoreCase(exec.trim())) {
                started = true;
                break;
            }
            Thread.sleep(1000);
        } while (counter++ < 30 && !started);
        hookService.call(hook.getContainerName(), hook.getRemoteExecAction());
    }

}
