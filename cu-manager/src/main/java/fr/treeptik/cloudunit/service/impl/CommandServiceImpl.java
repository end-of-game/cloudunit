package fr.treeptik.cloudunit.service.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import fr.treeptik.cloudunit.dto.Command;
import fr.treeptik.cloudunit.dto.ContainerUnit;
import fr.treeptik.cloudunit.dto.FileUnit;
import fr.treeptik.cloudunit.enums.RemoteExecAction;
import fr.treeptik.cloudunit.exception.ServiceException;
import fr.treeptik.cloudunit.service.ApplicationService;
import fr.treeptik.cloudunit.service.CommandService;
import fr.treeptik.cloudunit.service.DockerService;
import fr.treeptik.cloudunit.service.FileService;

@Service
public class CommandServiceImpl implements CommandService {

	@Inject
	private ApplicationService applicationService;

	@Inject
	private FileService fileService;

	@Inject
	private DockerService dockerService;

	private Logger logger = LoggerFactory.getLogger(CommandServiceImpl.class);

	@Override
	public List<Command> listCommandByContainer(String applicationName, String containerName) throws ServiceException {
		if (containerName == null)
			throw new ServiceException("The container name is empty");
		List<FileUnit> fileUnits = fileService.listByContainerIdAndPath(
				applicationService.listContainers(applicationName).stream()
						.filter(v -> v.getName().equals(containerName)).findFirst().get().getName(),
				dockerService.getEnv(containerName, "CU_SCRIPTS") + "/custom_scripts/");
		List<Command> commands = new ArrayList<>();
		BufferedReader bf = null;
		try {
			for (FileUnit fileUnit : fileUnits) {
				List<String> arguments = new ArrayList<>();
				Integer number = 0;
				String content = dockerService.execCommand(containerName, "cat " + fileUnit.getBreadcrump());
				bf = new BufferedReader(new StringReader(content));
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
			StringBuilder msgError = new StringBuilder(128);
			msgError.append(containerName);
			msgError.append(",").append(applicationName);
			logger.error(msgError.toString(), e);
		} finally {
			if (bf != null) try {
				bf.close();
			} catch (IOException e) {
			}
		}
		return commands;
	}

	@Override
	public String execCommand(Command command, String containerName, String applicationName) throws ServiceException {
		if (command.getName() == null)
			throw new ServiceException("The filename is empty");

		if (containerName == null)
			throw new ServiceException("The container name is empty");

		String output =  null;
		try {
			List<ContainerUnit> containerUnits = applicationService.listContainers(applicationName);
			String containerId = containerUnits.stream().filter(v -> v.getName().equals(containerName)).findFirst().get().getId();

			String customScriptPathFiles = dockerService.getEnv(containerName, "CU_SCRIPTS") + "/custom_scripts/";
			List<FileUnit> fileUnits = fileService.listByContainerIdAndPath(containerId,customScriptPathFiles);
			String commandLine = fileUnits.stream().filter(v -> v.getName().equals(command.getName())).findFirst().get().getBreadcrump();
			commandLine = commandLine + " " + command.getArguments().stream().map(v -> v + " ").collect(Collectors.joining());

			// Warning : do not forget * at the end of the command
			dockerService.execCommand(containerName, RemoteExecAction.CHMOD_PLUSX.getCommand() + " " + customScriptPathFiles + "*", true);

			// Execute the raw commad through the chosen file with its arguments
			output = dockerService.execCommand(containerName, commandLine);

		} catch (Exception e) {
			StringBuilder msgError = new StringBuilder(128);
			msgError.append(command);
			msgError.append(",").append(containerName);
			msgError.append(",").append(applicationName);
			logger.error(msgError.toString(), e);
		}
		return output;
	}

	public Command fileUnitToCommand(FileUnit fileUnit, Integer number, List<String> names) {
		Command command = new Command(fileUnit.getName(), number, names);
		return command;
	}
}
