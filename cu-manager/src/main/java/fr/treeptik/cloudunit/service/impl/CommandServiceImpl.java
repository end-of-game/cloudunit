package fr.treeptik.cloudunit.service.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import fr.treeptik.cloudunit.dto.Command;
import fr.treeptik.cloudunit.dto.FileUnit;
import fr.treeptik.cloudunit.exception.ServiceException;
import fr.treeptik.cloudunit.service.CommandService;
import fr.treeptik.cloudunit.service.DockerService;
import fr.treeptik.cloudunit.service.FileService;

@Service
public class CommandServiceImpl implements CommandService {
	@Inject
	private FileService fileService;

	@Inject
	private DockerService dockerService;

	private Logger logger = LoggerFactory.getLogger(CommandServiceImpl.class);

	@Override
	public List<Command> listCommandByContainer(String containerId) throws ServiceException {
		if (containerId == null)
			throw new ServiceException("The container name is empty");
		
		String customScriptsPath = dockerService.getEnv(containerId, "CU_SCRIPTS") + "/custom_scripts/";
        List<FileUnit> fileUnits = fileService.listByContainerIdAndPath(containerId, customScriptsPath);
        
		List<Command> commands = new ArrayList<>();
		BufferedReader bf = null;
		try {
			for (FileUnit fileUnit : fileUnits) {
				List<String> arguments = new ArrayList<>();
				String content = dockerService.execCommand(containerId, "cat " + fileUnit.getBreadcrump());
				bf = new BufferedReader(new StringReader(content));
				String line;
				int c = 0;
				while ((line = bf.readLine()) != null) {
					c++;
					if (!line.equals("") && Character.isUpperCase(line.charAt(0))) {
						arguments.add(line.split("=")[0]);
					}
					if (line.equals("") && c > 2)
						break;
				}
				Command command = fileUnitToCommand(fileUnit, arguments);
				commands.add(command);
			}
		} catch (Exception e) {
			logger.error(containerId, e);
		} finally {
			if (bf != null) try {
				bf.close();
			} catch (IOException e) {
			}
		}
		return commands;
	}

	@Override
	public String execCommand(String containerId, Command command, List<String> arguments) throws ServiceException {
		if (command.getName() == null)
			throw new ServiceException("The filename is empty");

		if (containerId == null)
			throw new ServiceException("The container name is empty");

		String output =  null;
		try {
		    String commandLine = command.getCommandLine(arguments);

			// Warning : do not forget * at the end of the command
			// dockerService.execCommand(containerId, RemoteExecAction.CHMOD_PLUSX.getCommand() + " " + customScriptPathFiles + "*", true);

			// Execute the raw command through the chosen file with its arguments
			output = dockerService.execCommand(containerId, commandLine);

		} catch (Exception e) {
			StringBuilder msgError = new StringBuilder();
			msgError.append(command);
			msgError.append(",").append(containerId);
			logger.error(msgError.toString(), e);
		}
		return output;
	}

	public Command fileUnitToCommand(FileUnit fileUnit, List<String> names) {
		Command command = new Command(fileUnit.getBreadcrump(), names);
		return command;
	}
}
