package fr.treeptik.cloudunit.service;

import fr.treeptik.cloudunit.dto.CommandRequest;
import fr.treeptik.cloudunit.exception.ServiceException;

import java.util.List;

public interface CommandService {

    void addCommand(CommandRequest commandRequest, String containerId, String applicationName) throws ServiceException;

    void deleteCommand(Integer id) throws ServiceException;

    void updateCommand(CommandRequest commandRequest, String containerId, String applicationName) throws ServiceException;

    CommandRequest getCommand(Integer id) throws ServiceException;

    List<CommandRequest> listCommandByImage(String applicationName, String containerId) throws ServiceException;
}
