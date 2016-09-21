package fr.treeptik.cloudunit.logs;

import fr.treeptik.cloudunit.exception.ServiceException;

/**
 * Created by nicolas on 20/09/2016.
 */
public interface GatheringStrategy {
    public String gather(String container, String source, int maxRows) throws ServiceException;
}
