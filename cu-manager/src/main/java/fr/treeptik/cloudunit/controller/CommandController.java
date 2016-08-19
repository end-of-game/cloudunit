package fr.treeptik.cloudunit.controller;


import fr.treeptik.cloudunit.dto.CommandRequest;
import fr.treeptik.cloudunit.dto.HttpOk;
import fr.treeptik.cloudunit.dto.JsonResponse;
import fr.treeptik.cloudunit.exception.ServiceException;
import fr.treeptik.cloudunit.model.Command;
import fr.treeptik.cloudunit.model.User;
import fr.treeptik.cloudunit.service.CommandService;
import fr.treeptik.cloudunit.service.EnvironmentService;
import fr.treeptik.cloudunit.utils.AuthentificationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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
    public @ResponseBody JsonResponse addCommand(@PathVariable String applicationName, @PathVariable String containerId,
                            @RequestBody CommandRequest commandRequest) throws ServiceException {
        logger.info("Add");
        User user = authentificationUtils.getAuthentificatedUser();
        try {
            commandService.addCommand(commandRequest, containerId, applicationName);
            return new HttpOk();
        } finally {
            authentificationUtils.allowUser(user);
        }
    }

    @RequestMapping(value = "/{applicationName}/container/{containerId}/command/{id}", method = RequestMethod.DELETE)
    public @ResponseBody JsonResponse deleteCommand(@PathVariable String applicationName, @PathVariable String containerId, @PathVariable Integer id) throws ServiceException {
        logger.info("Delete");
        User user = authentificationUtils.getAuthentificatedUser();
        try {
            commandService.deleteCommand(id);

            return new HttpOk();
        } finally {
            authentificationUtils.allowUser(user);
        }
    }

    @RequestMapping(value = "/{applicationName}/container/{containerId}/command/{id}", method = RequestMethod.PUT)
    public @ResponseBody JsonResponse updateCommand(@PathVariable String applicationName, @PathVariable String containerId,
                                                    @PathVariable Integer id, @RequestBody CommandRequest commandRequest) throws ServiceException {
        logger.info("Update");
        User user = authentificationUtils.getAuthentificatedUser();
        try {
            commandService.updateCommand(commandRequest, containerId, applicationName, id);
            return new HttpOk();
        } finally {
            authentificationUtils.allowUser(user);
        }
    }

    @RequestMapping(value = "/{applicationName}/container/{containerId}/command", method = RequestMethod.GET)
    public @ResponseBody List<CommandRequest> listCommandByImage(@PathVariable String applicationName,
                                                                @PathVariable String containerId) throws ServiceException {
        logger.info("Load by container");
        User user = authentificationUtils.getAuthentificatedUser();
        try {
            List<CommandRequest> commandRequestList = commandService.listCommandByImage(applicationName, containerId);

            return commandRequestList;
        } finally {
            authentificationUtils.allowUser(user);
        }
    }

    @RequestMapping(value = "/{applicationName}/container/{containerId}/command/{id}", method = RequestMethod.POST)
    public @ResponseBody CommandRequest getCommand(@PathVariable String applicationName, @PathVariable String containerId,
                                     @PathVariable Integer id) throws ServiceException {
        logger.info("Load by id");
        User user = authentificationUtils.getAuthentificatedUser();
        try {
            CommandRequest commandRequest = commandService.getCommand(id);

            return commandRequest;
        } finally {
            authentificationUtils.allowUser(user);
        }
    }
}
