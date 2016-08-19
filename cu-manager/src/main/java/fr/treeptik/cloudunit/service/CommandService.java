package fr.treeptik.cloudunit.service;

import fr.treeptik.cloudunit.dto.CommandRequest;
import fr.treeptik.cloudunit.exception.ServiceException;

import java.util.List;

public interface CommandService {

    void addCommand(CommandRequest commandRequest) throws ServiceException;

    void deleteCommand(Integer id) throws ServiceException;

    void updateCommand(CommandRequest commandRequest) throws ServiceException;

    List<CommandRequest> listCommandByContainer(String containerId) throws ServiceException;

    CommandRequest getCommand(Integer id) throws ServiceException;
}
