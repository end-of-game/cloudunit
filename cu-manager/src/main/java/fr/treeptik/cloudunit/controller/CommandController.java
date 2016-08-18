package fr.treeptik.cloudunit.controller;


import fr.treeptik.cloudunit.dto.CommandRequest;
import fr.treeptik.cloudunit.exception.ServiceException;
import fr.treeptik.cloudunit.model.Command;
import fr.treeptik.cloudunit.model.User;
import fr.treeptik.cloudunit.service.CommandService;
import fr.treeptik.cloudunit.service.EnvironmentService;
import fr.treeptik.cloudunit.utils.AuthentificationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/application")
public class CommandController {

    private final Logger logger = LoggerFactory.getLogger(EnvironmentController.class);

    @Inject
    private AuthentificationUtils authentificationUtils;

    @Inject
    private CommandService commandService;

    @RequestMapping(value = "/{applicationName}/container/{containerId}/command", method = RequestMethod.POST)
    public CommandRequest addCommand(@PathVariable String applicationName, @PathVariable String containerId,
                                     @RequestBody CommandRequest commandRequest) throws ServiceException {
        logger.info("Add");
        User user = authentificationUtils.getAuthentificatedUser();
        try {
            commandService.addCommand(commandRequest);
            return commandRequest;
        } finally {
            authentificationUtils.allowUser(user);
        }
    }

    @RequestMapping(value = "/{applicationName}/container/{containerId}/command", method = RequestMethod.DELETE)
    public void deleteCommand(@PathVariable String applicationName, @PathVariable String containerId,
                              @RequestBody CommandRequest commandRequest) throws ServiceException {
        logger.info("Delete");
        User user = authentificationUtils.getAuthentificatedUser();
        try {
            commandService.deleteCommand(commandRequest.getId());
        } finally {
            authentificationUtils.allowUser(user);
        }
    }

    @RequestMapping(value = "/{applicationName}/container/{containerId}/command", method = RequestMethod.PUT)
    public CommandRequest updateCommand(@PathVariable String applicationName, @PathVariable String containerId,
                                        @RequestBody CommandRequest commandRequest) throws ServiceException {
        logger.info("Update");
        User user = authentificationUtils.getAuthentificatedUser();
        try {
            commandService.updateCommand(commandRequest);
            return commandRequest;
        } finally {
            authentificationUtils.allowUser(user);
        }
    }

    @RequestMapping(value = "/{applicationName}/container/{containerId}/command", method = RequestMethod.GET)
    public List<CommandRequest> listCommandByContainer(@PathVariable String applicationName,
                                                       @PathVariable String containerId) throws ServiceException {
        logger.info("Load by container");
        User user = authentificationUtils.getAuthentificatedUser();
        try {
            List<Command> commandList = commandService.listCommandByContainer(containerId);
            List<CommandRequest> commandRequestList = new ArrayList<>();

            for (Command command : commandList) {
                List<String> arguments = new ArrayList<>(command.getArguments());

                CommandRequest commandRequest = new CommandRequest();
                commandRequest.setId(command.getId());
                commandRequest.setValue(command.getValue());
                commandRequest.setArguments(arguments);
                commandRequest.setContainerId(command.getContainerId());
                commandRequestList.add(commandRequest);
            }

            return commandRequestList;
        } finally {
            authentificationUtils.allowUser(user);
        }
    }

    @RequestMapping(value = "/{applicationName}/container/{containerId}/command/{id}", method = RequestMethod.POST)
    public CommandRequest getCommand(@PathVariable String applicationName, @PathVariable String containerId,
                                     @PathVariable Integer id) throws ServiceException {
        logger.info("Load by id");
        User user = authentificationUtils.getAuthentificatedUser();
        try {
            Command command = commandService.getCommand(id);

            List<String> arguments = new ArrayList<>(command.getArguments());
            CommandRequest commandRequest = new CommandRequest();
            commandRequest.setId(command.getId());
            commandRequest.setValue(command.getValue());
            commandRequest.setArguments(arguments);
            commandRequest.setContainerId(command.getContainerId());

            return commandRequest;
        } finally {
            authentificationUtils.allowUser(user);
        }
    }
}
