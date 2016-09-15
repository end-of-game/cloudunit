package fr.treeptik.cloudunit.cli.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;

import fr.treeptik.cloudunit.cli.utils.ServerUtils;
import fr.treeptik.cloudunit.cli.utils.VolumeService;

@Component
public class VolumeCommands implements CommandMarker {

	@Autowired
	private VolumeService volumeService;

	@Autowired
	private ServerUtils serverUtils;

	@CliCommand(value = "create-volume", help = "Create a new volume")
	public String createVolume(
			@CliOption(key = { "name" }, mandatory = true, help = "Name of the local volume") String name) {
		return volumeService.createVolume(name);
	}

	@CliCommand(value = "rm-volume", help = "Remove a volume")
	public String removeVolume(
			@CliOption(key = {"name"}, mandatory = true, help = "Name of the deleted volume") String name) {
		return volumeService.removeVolume(name);
	}

	@CliCommand(value = "mount-volume", help = "Update a volume")
	public String mountVolume(
			@CliOption(key = {"volume-name"}, mandatory = true, help = "Name of the volume") String name,
			@CliOption(key = {"path"}, mandatory = true, help = "Path in the container") String path,
			@CliOption(key = {"read-only"}, unspecifiedDefaultValue = "false", mandatory = false, help = "Mode read-only") boolean mode,
			@CliOption(key = {"container-name"}, mandatory = false, help = "Container for the volume") String containerName,
			@CliOption(key = {"application-name"}, mandatory = false, help = "Application for the volume") String applicationName) {
		return serverUtils.mountVolume(name, path, mode, containerName, applicationName);
	}

	@CliCommand(value = "unmount-volume", help = "Update a volume")
	public String mountVolume(
			@CliOption(key = {"volume-name"}, mandatory = true, help = "Name of the volume") String name,
			@CliOption(key = {"container-name"}, mandatory = true, help = "Container for the volume") String containerName) {
		return serverUtils.unmountVolume(name, containerName);
	}

	@CliCommand(value = "list-volumes", help = "Display all volumes")
	public String displayVolumes() {
		return volumeService.displayVolumes();
	}
}
