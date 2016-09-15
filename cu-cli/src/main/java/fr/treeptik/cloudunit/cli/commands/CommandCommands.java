package fr.treeptik.cloudunit.cli.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;

import fr.treeptik.cloudunit.cli.utils.ApplicationUtils;

@Component
public class CommandCommands implements CommandMarker {
    @Autowired
    private ApplicationUtils applicationUtils;

    @CliCommand(value = "list-commands", help = "List all commands")
    public String listCommands(
            @CliOption(key = { "container-name" }, mandatory = true, help = "Name of the container") String containerName) {
        return applicationUtils.listCommands(containerName);
    }

    @CliCommand(value = "exec-command", help = "Execute a command")
    public String execCommands(
            @CliOption(key = { "container-name" }, mandatory = true, help = "Name of the container") String containerName,
            @CliOption(key = { "name" }, mandatory = true, help = "Name of the command") String name,
            @CliOption(key = {"arguments"}, mandatory = true, help = "Arguments for the command split by ','") String arguments) {
        return applicationUtils.execCommand(name, containerName, arguments);
    }
}
