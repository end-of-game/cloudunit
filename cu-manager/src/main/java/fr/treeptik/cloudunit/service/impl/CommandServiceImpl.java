package fr.treeptik.cloudunit.service.impl;

import fr.treeptik.cloudunit.dao.CommandDAO;
import fr.treeptik.cloudunit.dto.CommandRequest;
import fr.treeptik.cloudunit.exception.ServiceException;
import fr.treeptik.cloudunit.model.Command;
import fr.treeptik.cloudunit.service.CommandService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommandServiceImpl implements CommandService {

    @Inject
    private CommandDAO commandDAO;

    @Override
    @Transactional
    public void addCommand(CommandRequest commandRequest) throws ServiceException {
        if(commandRequest.getValue() == null)
            throw new ServiceException("The value is empty");

        if(commandRequest.getDescription() == null)
            throw new ServiceException("The description is empty");

        Command command = new Command();

        command.setValue(commandRequest.getValue());
        command.setArguments(commandRequest.getArguments());
        command.setContainerId(commandRequest.getContainerId());
        commandDAO.save(command);
    }

    @Override
    @Transactional
    public void deleteCommand(Integer id) throws ServiceException {
        if(id == null)
            throw new ServiceException("Id is null");

        commandDAO.delete(id);
    }

    @Override
    @Transactional
    public void updateCommand(CommandRequest commandRequest) throws ServiceException {
        if(commandRequest.getValue() == null)
            throw new ServiceException("The value is empty");

        if(commandRequest.getDescription() == null)
            throw new ServiceException("The description is empty");

        Command command = new Command();
        command.setId(commandRequest.getId());
        command.setValue(commandRequest.getValue());
        command.setArguments(commandRequest.getArguments());
        command.setContainerId(commandRequest.getContainerId());
        commandDAO.save(command);
    }

    @Override
    public List<CommandRequest> listCommandByContainer(String containerId) throws ServiceException {
        if(containerId == null)
            throw new ServiceException("The container id is empty");

        List<Command> commandList = commandDAO.findByContainer(containerId);

        return commandList.stream().map(v -> v.mapToRequest()).collect(Collectors.toList());
    }

    @Override
    public CommandRequest getCommand(Integer id) throws ServiceException {
        if(id == null)
            throw new ServiceException("The id is empty");

        return commandDAO.findById(id).mapToRequest();
    }
}
