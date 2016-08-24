package fr.treeptik.cloudunit.service;

import fr.treeptik.cloudunit.dto.Command;
import fr.treeptik.cloudunit.exception.ServiceException;

import java.util.List;

public interface CommandService {

    List<Command> listCommandByContainer(String applicationName, String containerName) throws ServiceException;

    void execCommand(Command command, String containerName, String applicationName) throws ServiceException;
}
