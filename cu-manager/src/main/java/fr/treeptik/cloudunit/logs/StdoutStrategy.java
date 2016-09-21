package fr.treeptik.cloudunit.logs;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import fr.treeptik.cloudunit.exception.ServiceException;
import fr.treeptik.cloudunit.service.DockerService;

/**
 * Created by nicolas on 20/09/2016.
 */
@Component("stdout")
public class StdoutStrategy implements GatheringStrategy {

    private Logger logger = LoggerFactory.getLogger(StdoutStrategy.class);

    @Inject
    private DockerService dockerService;

    @Override
    public String gather(String container, String source, int maxRows) throws ServiceException {
        String logs = "";
        try {
            logs = dockerService.logs(container);
        } catch (Exception e) {
            logger.error(container + "," + source, e);
        }
        return logs;
    }
}
