package fr.treeptik.cloudunit.cli.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.treeptik.cloudunit.cli.commands.ShellStatusCommand;
import fr.treeptik.cloudunit.cli.exception.ManagerResponseException;
import fr.treeptik.cloudunit.cli.processor.InjectLogger;
import fr.treeptik.cloudunit.cli.rest.RestUtils;

@Component
public class VolumeService {

	@InjectLogger
	private Logger log;

	@Autowired
	private AuthentificationUtils authentificationUtils;

	@Autowired
	private RestUtils restUtils;

	@Autowired
	private ShellStatusCommand statusCommand;

	/**
	 * @param applicationName
	 * @param portToOpen
	 * @return
	 */
	public String createVolume(String name) {

		try {
			Map<String, String> parameters = new HashMap<>();
			parameters.put("name", name);
			restUtils.sendPostCommand(authentificationUtils.finalHost + "/volume",
					authentificationUtils.getMap(), parameters);

		} catch (ManagerResponseException e) {
			statusCommand.setExitStatut(1);
			return ANSIConstants.ANSI_RED + e.getMessage() + ANSIConstants.ANSI_RESET;
		}

		statusCommand.setExitStatut(0);

		return "The volume " + name + " was been successfully created";
	}
}
