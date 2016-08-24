package fr.treeptik.cloudunit.controller;

import java.util.List;

import javax.inject.Inject;

import fr.treeptik.cloudunit.dto.Command;
import fr.treeptik.cloudunit.dto.FileUnit;
import fr.treeptik.cloudunit.dto.HttpOk;
import fr.treeptik.cloudunit.dto.JsonResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import fr.treeptik.cloudunit.exception.ServiceException;
import fr.treeptik.cloudunit.model.User;
import fr.treeptik.cloudunit.service.CommandService;
import fr.treeptik.cloudunit.utils.AuthentificationUtils;

@Controller
@RequestMapping("/application")
public class CommandController {

    private final Logger logger = LoggerFactory.getLogger(EnvironmentController.class);

    @Inject
    private AuthentificationUtils authentificationUtils;

    @Inject
    private CommandService commandService;

    @RequestMapping(value = "/{applicationName}/container/{containerName}/command", method = RequestMethod.GET)
    public @ResponseBody List<Command> listCommandByImage(@PathVariable String applicationName,
                                                           @PathVariable String containerName) throws ServiceException {
        logger.info("Load by container");
        User user = authentificationUtils.getAuthentificatedUser();
        try {
            List<Command> commands = commandService.listCommandByContainer(applicationName, containerName);

            return commands;
        } finally {
            authentificationUtils.allowUser(user);
        }
    }

    @RequestMapping(value = "/{applicationName}/container/{containerName}/command/{filename}/exec", method = RequestMethod.POST)
    public @ResponseBody JsonResponse execCommand(@PathVariable String applicationName, @PathVariable String containerName,
                                                  @PathVariable String filename, @RequestBody List<String> arguments) throws ServiceException {
        logger.info("Execute by id");
        User user = authentificationUtils.getAuthentificatedUser();
        try {
            Command command = new Command();
            command.setName(filename);
            command.setArgumentNumber(arguments.size());
            command.setArguments(arguments);
            commandService.execCommand(command, containerName, applicationName);

            return new HttpOk();
        } finally {
            authentificationUtils.allowUser(user);
        }
    }
}
