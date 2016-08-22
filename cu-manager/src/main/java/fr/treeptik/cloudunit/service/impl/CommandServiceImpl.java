package fr.treeptik.cloudunit.service.impl;

import fr.treeptik.cloudunit.dao.CommandDAO;
import fr.treeptik.cloudunit.dto.CommandRequest;
import fr.treeptik.cloudunit.dto.ContainerUnit;
import fr.treeptik.cloudunit.exception.CheckException;
import fr.treeptik.cloudunit.exception.FatalDockerJSONException;
import fr.treeptik.cloudunit.exception.ServiceException;
import fr.treeptik.cloudunit.model.Application;
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

    @Inject
    private DockerService dockerService;

    @Override
    @Transactional
    public void addCommand(CommandRequest commandRequest, String containerName, String applicationName) throws ServiceException {
        if(commandRequest.getValue() == null)
            throw new ServiceException("The value is empty");

        if(commandRequest.getDescription() == null)
            throw new ServiceException("The description is empty");

        List<Command> commandList = commandDAO.findAll();
        Optional<Command> value = commandList.stream().filter(v ->
                v.getValue().equals(commandRequest.getValue()))
                .findFirst();

        if(value.isPresent())
            throw new CheckException("This value already exists");

        List<ContainerUnit> containerUnits = applicationService.listContainers(applicationName);
        String type = containerUnits.stream().filter(v -> v.getName().equals(containerName)).findFirst().get().getType();
        Integer imageId = type.equals("server") ? serverService.findByName(containerName).getImage().getId() :
                moduleService.findByName(containerName).getImage().getId();
        Image image = imageService.findById(imageId);


        Command command = new Command();
        command.setValue(commandRequest.getValue());
        command.setDescription(commandRequest.getDescription());
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
    public void updateCommand(CommandRequest commandRequest, String containerName, String applicationName, Integer id) throws ServiceException {
        if(commandRequest.getValue() == null)
            throw new ServiceException("The value is empty");

        if(commandRequest.getDescription() == null)
            throw new ServiceException("The description is empty");

        Command oldCommand = commandDAO.findById(id);
        List<Command> commandList = commandDAO.findAll();
        Optional<Command> value = commandList.stream().filter(v ->
                v.getValue().equals(commandRequest.getValue()) && !v.getValue().equals(oldCommand.getValue()))
                .findFirst();

        if(value.isPresent())
            throw new CheckException("This value already exists");

        List<ContainerUnit> containerUnits = applicationService.listContainers(applicationName);
        String type = containerUnits.stream().filter(v -> v.getName().equals(containerName)).findFirst().get().getType();
        Integer imageId = type.equals("server") ? serverService.findByName(containerName).getImage().getId() :
                moduleService.findByName(containerName).getImage().getId();
        Image image = imageService.findById(imageId);

        Command command = new Command();
        command.setId(id);
        command.setValue(commandRequest.getValue());
        command.setDescription(commandRequest.getDescription());
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
    public List<CommandRequest> listCommandByImage(String applicationName, String containerName) throws ServiceException {
        if (containerName == null)
            throw new ServiceException("The container name is empty");

        List<ContainerUnit> containerUnits = applicationService.listContainers(applicationName);
        String type = containerUnits.stream().filter(v -> v.getName().equals(containerName)).findFirst().get().getType();
        Integer imageId = type.equals("server") ? serverService.findByName(containerName).getImage().getId() :
                moduleService.findByName(containerName).getImage().getId();
        List<CommandRequest> commandRequestList = commandDAO.findByImage(imageId).stream()
                .map(v -> v.mapToRequest()).collect(Collectors.toList());

        return commandRequestList;
    }

    @Override
    public String execCommand(Integer id, String containerName, String applicationName) throws ServiceException {
        if(id == null)
            throw new ServiceException("The id is empty");

        if (containerName == null)
            throw new ServiceException("The container name is empty");

        String result = "";
        try{
            Command command = commandDAO.findById(id);
            String commandString = command.getValue() + " " + command.getArguments().stream().map(v -> v + " ").collect(Collectors.joining());
            List<ContainerUnit> containerUnits = applicationService.listContainers(applicationName);
            ContainerUnit containerUnit = containerUnits.stream().filter(v -> v.getName().equals(containerName)).findFirst().get();
            result = dockerService.execCommand(containerUnit.getName(), command.getValue());
            return result;
        } catch (FatalDockerJSONException e) {
            e.printStackTrace();
        }
        return result;
    }
}
