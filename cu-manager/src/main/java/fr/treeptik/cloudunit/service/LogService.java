package fr.treeptik.cloudunit.service;

import fr.treeptik.cloudunit.exception.ServiceException;
import fr.treeptik.cloudunit.json.ui.LogUnit;

import java.util.List;

/**
 * Created by nicolas on 25/08/2014.
 */
public interface LogService {

    public List<LogUnit> listByApp(String applicationName, String containerId, String source, Integer nbRows)
            throws ServiceException;

    public int deleteLogsForApplication(String applicationName) throws ServiceException;
}