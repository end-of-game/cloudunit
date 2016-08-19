package fr.treeptik.cloudunit.service.impl;

import fr.treeptik.cloudunit.dao.CommandDAO;
import fr.treeptik.cloudunit.dto.CommandRequest;
import fr.treeptik.cloudunit.model.Command;
import fr.treeptik.cloudunit.service.CommandService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class CommandServiceImpl implements CommandService {

    @Inject
    private CommandDAO commandDAO;

    @Override
    @Transactional
    public void addCommand(CommandRequest commandRequest) {
        Set<String> arguments = new HashSet<String>(commandRequest.getArguments());
        Command command = new Command();
        command.setValue(commandRequest.getValue());
        command.setArguments(arguments);
        command.setContainerId(commandRequest.getContainerId());
        commandDAO.save(command);
    }

    @Override
    @Transactional
    public void deleteCommand(Integer id) {
        commandDAO.delete(id);
    }

    @Override
    @Transactional
    public void updateCommand(CommandRequest commandRequest) {
        Set<String> arguments = new HashSet<String>(commandRequest.getArguments());
        Command command = new Command();
        command.setId(commandRequest.getId());
        command.setValue(commandRequest.getValue());
        command.setArguments(arguments);
        command.setContainerId(commandRequest.getContainerId());
        commandDAO.save(command);
    }

    @Override
    public List<Command> listCommandByContainer(String containerId) {
        return commandDAO.findByContainer(containerId);
    }

    @Override
    public Command getCommand(Integer id) {
        return commandDAO.findById(id);
    }
}
