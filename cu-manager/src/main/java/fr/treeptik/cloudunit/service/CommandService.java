package fr.treeptik.cloudunit.service;

import fr.treeptik.cloudunit.dto.Command;
import fr.treeptik.cloudunit.exception.ServiceException;

import java.util.List;

public interface CommandService {

    List<Command> listCommandByContainer(String containerId) throws ServiceException;

    String execCommand(String containerId, Command command, List<String> arguments) throws ServiceException;
}
