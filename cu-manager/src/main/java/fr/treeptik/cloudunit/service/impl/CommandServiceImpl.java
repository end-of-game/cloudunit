package fr.treeptik.cloudunit.service.impl;

import fr.treeptik.cloudunit.dto.Command;
import fr.treeptik.cloudunit.dto.ContainerUnit;
import fr.treeptik.cloudunit.dto.FileUnit;
import fr.treeptik.cloudunit.exception.ServiceException;
import fr.treeptik.cloudunit.service.*;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommandServiceImpl implements CommandService {

	@Inject
	private ApplicationService applicationService;

	@Inject
	private FileService fileService;

	@Inject
	private DockerService dockerService;

	@Override
	public List<Command> listCommandByContainer(String applicationName, String containerName) throws ServiceException {
		if (containerName == null)
			throw new ServiceException("The container name is empty");
		List<FileUnit> fileUnits = fileService.listByContainerIdAndPath(
				applicationService.listContainers(applicationName).stream()
						.filter(v -> v.getName().equals(containerName)).findFirst().get().getId(),
				dockerService.getEnv(containerName, "CU_SCRIPTS") + "/custom-scripts/");

		List<Command> commands = new ArrayList<>();

		try {
			for (FileUnit fileUnit : fileUnits) {
				List<String> arguments = new ArrayList<>();
				Integer number = 0;
				String content = dockerService.execCommand(containerName, "cat " + fileUnit.getBreadcrump());
				BufferedReader bf = new BufferedReader(new StringReader(content));
				String line;
				int c = 0;
				while ((line = bf.readLine()) != null) {
					c++;
					if (!line.equals("") && Character.isUpperCase(line.charAt(0))) {
						arguments.add(line.split("=")[0]);
						number = Character.getNumericValue(line.split("=")[1].charAt(1));
					}
					if (line.equals("") && c > 2)
						break;
				}
				Command command = fileUnitToCommand(fileUnit, number, arguments);
				commands.add(command);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return commands;
	}

	@Override
	public void execCommand(Command command, String containerName, String applicationName) throws ServiceException {
		if (command.getName() == null)
			throw new ServiceException("The filename is empty");

		if (containerName == null)
			throw new ServiceException("The container name is empty");

		try {
			List<ContainerUnit> containerUnits = applicationService.listContainers(applicationName);
			String containerId = containerUnits.stream().filter(v -> v.getName().equals(containerName)).findFirst()
					.get().getId();

			List<FileUnit> fileUnits = fileService.listByContainerIdAndPath(containerId,
					dockerService.getEnv(containerName, "CU_SCRIPTS") + "/custom-scripts/");
			String commandLine = fileUnits.stream().filter(v -> v.getName().equals(command.getName())).findFirst().get()
					.getBreadcrump();
			commandLine = commandLine + " "
					+ command.getArguments().stream().map(v -> v + " ").collect(Collectors.joining());

			dockerService.execCommand(containerName, commandLine);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Command fileUnitToCommand(FileUnit fileUnit, Integer number, List<String> names) {
		Command command = new Command();
		command.setName(fileUnit.getName());
		command.setArgumentNumber(number);
		command.setArguments(names);
		return command;
	}
}
