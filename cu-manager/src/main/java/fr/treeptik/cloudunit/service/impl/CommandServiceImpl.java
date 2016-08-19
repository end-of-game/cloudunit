package fr.treeptik.cloudunit.service.impl;

import fr.treeptik.cloudunit.dao.CommandDAO;
import fr.treeptik.cloudunit.dto.CommandRequest;
import fr.treeptik.cloudunit.dto.ContainerUnit;
import fr.treeptik.cloudunit.exception.ServiceException;
import fr.treeptik.cloudunit.model.Command;
import fr.treeptik.cloudunit.model.Image;
import fr.treeptik.cloudunit.service.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CommandServiceImpl implements CommandService {

    @Inject
    private CommandDAO commandDAO;

    @Inject
    private ApplicationService applicationService;

    @Inject
    private ModuleService moduleService;

    @Inject
    private ServerService serverService;

    @Inject
    private ImageService imageService;

    @Override
    @Transactional
    public void addCommand(CommandRequest commandRequest, String containerId, String applicationName) throws ServiceException {
        if(commandRequest.getValue() == null)
            throw new ServiceException("The value is empty");

        if(commandRequest.getDescription() == null)
            throw new ServiceException("The description is empty");

        List<ContainerUnit> containerUnits = applicationService.listContainers(applicationName);
        String type = containerUnits.stream().filter(v -> v.getId().equals(containerId)).findFirst().get().getType();
        Integer imageId = type.equals("server") ? serverService.findByContainerID(containerId).getImage().getId() :
                moduleService.findByContainerID(containerId).getImage().getId();
        Image image = imageService.findById(imageId);


        Command command = new Command();
        command.setValue(commandRequest.getValue());
        command.setArguments(commandRequest.getArguments());
        command.setImage(image);
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
    public void updateCommand(CommandRequest commandRequest, String containerId, String applicationName) throws ServiceException {
        if(commandRequest.getValue() == null)
            throw new ServiceException("The value is empty");

        if(commandRequest.getDescription() == null)
            throw new ServiceException("The description is empty");

        List<ContainerUnit> containerUnits = applicationService.listContainers(applicationName);
        String type = containerUnits.stream().filter(v -> v.getId().equals(containerId)).findFirst().get().getType();
        Integer imageId = type.equals("server") ? serverService.findByContainerID(containerId).getImage().getId() :
                moduleService.findByContainerID(containerId).getImage().getId();
        Image image = imageService.findById(imageId);

        Command command = new Command();
        command.setId(commandRequest.getId());
        command.setValue(commandRequest.getValue());
        command.setArguments(commandRequest.getArguments());
        command.setImage(image);
        commandDAO.save(command);
    }

    @Override
    public CommandRequest getCommand(Integer id) throws ServiceException {
        if(id == null)
            throw new ServiceException("The id is empty");

        return commandDAO.findById(id).mapToRequest();
    }

    @Override
    public List<CommandRequest> listCommandByImage(String applicationName, String containerId) throws ServiceException {
        if (containerId == null)
            throw new ServiceException("The container id is empty");

        List<ContainerUnit> containerUnits = applicationService.listContainers(applicationName);
        String type = containerUnits.stream().filter(v -> v.getId().equals(containerId)).findFirst().get().getType();
        Integer imageId = type.equals("server") ? serverService.findByContainerID(containerId).getImage().getId() :
                moduleService.findByContainerID(containerId).getImage().getId();
        List<CommandRequest> commandRequestList = commandDAO.findByImage(imageId).stream()
                .map(v -> v.mapToRequest()).collect(Collectors.toList());

        return commandRequestList;
    }
}
