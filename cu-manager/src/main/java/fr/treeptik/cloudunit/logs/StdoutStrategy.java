package fr.treeptik.cloudunit.logs;

import fr.treeptik.cloudunit.controller.LogController;
import fr.treeptik.cloudunit.dto.LogResource;
import fr.treeptik.cloudunit.exception.ServiceException;
import fr.treeptik.cloudunit.service.DockerService;
import fr.treeptik.cloudunit.service.FileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Created by nicolas on 20/09/2016.
 */
@Component("stdout")
public class StdoutStrategy implements GatheringStrategy<String, Integer> {

    private Logger logger = LoggerFactory.getLogger(StdoutStrategy.class);

    @Inject
    private DockerService dockerService;

    @Override
    public String gather(String container, String source, Integer nbRows) throws ServiceException {
        String logs = "";
        try {
            logs = dockerService.logs(container);
        } catch (Exception e) {
            logger.error(container + "," + source, e);
        }
        return logs;
    }
}
