package fr.treeptik.cloudunit.logs;

import fr.treeptik.cloudunit.exception.ServiceException;
import fr.treeptik.cloudunit.service.FileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

/**
 * Created by nicolas on 20/09/2016.
 */
@Component("tail")
public class TailStrategy implements GatheringStrategy<String, Integer> {

    private Logger logger = LoggerFactory.getLogger(TailStrategy.class);

    @Inject
    private FileService fileService;

    @Override
    public String gather(String container, String source, Integer nbRows) throws ServiceException {
        String logs = "";
        try {
            logs = fileService.tailFileForNLines(container, source, nbRows);
        } catch (Exception e) {
            logger.error(container + "," + source, e);
        }
        return logs;
    }
}
