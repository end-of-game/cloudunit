package fr.treeptik.cloudunit.cli.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;

import fr.treeptik.cloudunit.cli.utils.VolumeService;

@Component
public class VolumeCommands implements CommandMarker {

	@Autowired
	private VolumeService volumeService;

	@CliCommand(value = "create-volume", help = "Create a new volume")
	public String createVolume(
			@CliOption(key = { "name" }, mandatory = true, help = "Name of the local volume") String name) {
		return volumeService.createVolume(name);
	}

	@CliCommand(value = "list-volumes", help = "Display all volumes")
	public String displayVolumes() {
		return null;
	}
}
