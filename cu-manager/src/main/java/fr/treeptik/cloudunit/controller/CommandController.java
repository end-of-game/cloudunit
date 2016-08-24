package fr.treeptik.cloudunit.controller;

import java.util.List;

import javax.inject.Inject;

import fr.treeptik.cloudunit.dto.Command;
import fr.treeptik.cloudunit.dto.HttpOk;
import fr.treeptik.cloudunit.dto.JsonResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import fr.treeptik.cloudunit.exception.ServiceException;
import fr.treeptik.cloudunit.service.CommandService;

@Controller
@RequestMapping("/application")
public class CommandController {

    private final Logger logger = LoggerFactory.getLogger(CommandController.class);

    @Inject
    private CommandService commandService;

    @RequestMapping(value = "/{applicationName}/container/{containerName}/command", method = RequestMethod.GET)
    public @ResponseBody List<Command> listCommandByImage(@PathVariable String applicationName,
                                                           @PathVariable String containerName) throws ServiceException {
        logger.info("Load by container");
        List<Command> commands = commandService.listCommandByContainer(applicationName, containerName);
        return commands;
    }

    @RequestMapping(value = "/{applicationName}/container/{containerName}/command/{filename}/exec", method = RequestMethod.POST,
        consumes = "application/json")
    public @ResponseBody JsonResponse execCommand(@PathVariable String applicationName, @PathVariable String containerName,
                                                  @PathVariable String filename, @RequestBody Command command) throws ServiceException {
        logger.info("Execute by filename");
        commandService.execCommand(command, containerName, applicationName);
        return new HttpOk();
    }
}
