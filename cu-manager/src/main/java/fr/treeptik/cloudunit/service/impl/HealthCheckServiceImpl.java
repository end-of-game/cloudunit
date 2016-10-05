package fr.treeptik.cloudunit.service.impl;

import fr.treeptik.cloudunit.docker.core.DockerCloudUnitClient;
import fr.treeptik.cloudunit.docker.model.State;
import fr.treeptik.cloudunit.exception.ServiceException;
import fr.treeptik.cloudunit.model.Application;
import fr.treeptik.cloudunit.model.Status;
import fr.treeptik.cloudunit.service.ApplicationService;
import fr.treeptik.cloudunit.service.HealthCheckService;
import fr.treeptik.cloudunit.service.ModuleService;
import fr.treeptik.cloudunit.service.ServerService;
import fr.treeptik.cloudunit.utils.ContainerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.List;

/**
 * Created by guillaume on 05/10/16.
 */
@Component
public class HealthCheckServiceImpl implements HealthCheckService {

    private Logger logger = LoggerFactory.getLogger(HealthCheckService.class);

    @Inject
    private ApplicationService applicationService;

    @Inject
    private ServerService serverService;

    @Inject
    private DockerCloudUnitClient dockerCloudUnitClient;

    @Inject
    private ModuleService moduleService;



    @Override
    public void checkAndRebootApplications(){
        try {
            List<Application> applications = applicationService.findAll();
            applications.stream().filter(a-> a.getStatus().equals(Status.START)).forEach(a->{
                checkNotStartedModules(a);
                checkNotStartedServer(a);
            });
            applications.stream().filter(a-> a.getStatus().equals(Status.STOP)).forEach(a->{
                checkNotStoppedModules(a);
                checkNotStoppedServer(a);
            });

        } catch (ServiceException e) {
            logger.error("An error occures when check and reboot started apps : "
                    + e.getLocalizedMessage());
            e.printStackTrace();
        }


    }

    private void checkNotStartedModules(Application a) {
        a.getModules().stream().forEach(m-> {
            final State moduleState = dockerCloudUnitClient.findContainer(
                    ContainerUtils.newStartInstance(m.getName(), null, null, false))
                    .getState();
            if(!moduleState.getRunning()){
                logger.warn("Module container is not started... Trying to restart it." );
                try {
                    moduleService.startModule(m.getName());
                } catch (ServiceException e) {
                    logger.error("An error occures when check and reboot started apps : "
                            + e.getLocalizedMessage());
                }
            }
        });
    }
    private void checkNotStoppedModules(Application a) {
        a.getModules().stream().forEach(m-> {
            final State moduleState = dockerCloudUnitClient.findContainer(
                    ContainerUtils.newStartInstance(m.getName(), null, null, false))
                    .getState();
            if(moduleState.getRunning()){
                logger.warn("Module container is not stopped... Trying to stop it." );
                try {
                    moduleService.stopModule(m.getName());
                } catch (ServiceException e) {
                    logger.error("An error occures when check and reboot shutdown apps : "
                            + e.getLocalizedMessage());
                }
            }
        });
    }

    private void checkNotStartedServer(Application a) {
        final State serverState = dockerCloudUnitClient.findContainer(
                ContainerUtils.newStartInstance(a.getServer().getName(), null, null, false))
                .getState();
        if(!serverState.getRunning()){
           logger.warn("Server container is not started... Trying to restart it." );
           try {
               serverService.startServer(a.getServer());
           } catch (ServiceException e) {
               logger.error("An error occures when check and reboot started apps : "
                       + e.getLocalizedMessage());
           }
       }
    }

    private void checkNotStoppedServer(Application a) {
        final State serverState = dockerCloudUnitClient.findContainer(
                ContainerUtils.newStartInstance(a.getServer().getName(), null, null, false))
                .getState();

        if(serverState.getRunning()){
            logger.warn("Server container is not stopped... Trying to stop it." );
            try {
                serverService.stopServer(a.getServer());
            } catch (ServiceException e) {
                logger.error("An error occures when check and shutdown apps : "
                        + e.getLocalizedMessage());
            }
        }
    }


}
