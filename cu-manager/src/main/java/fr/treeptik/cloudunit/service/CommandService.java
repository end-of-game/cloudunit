package fr.treeptik.cloudunit.service;

import fr.treeptik.cloudunit.dto.CommandRequest;
import fr.treeptik.cloudunit.model.Command;

import java.util.List;

public interface CommandService {

    void addCommand(CommandRequest commandRequest);

    void deleteCommand(Integer id);

    void updateCommand(CommandRequest commandRequest);

    List<Command> listCommandByContainer(String containerId);

    Command getCommand(Integer id);
}
