package fr.treeptik.cloudunit.cli.commands;

import java.text.MessageFormat;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;

import fr.treeptik.cloudunit.cli.tables.CommandTableColumn;
import fr.treeptik.cloudunit.cli.utils.ApplicationUtils;
import fr.treeptik.cloudunit.dto.Command;

@Component
public class CommandCommands implements CommandMarker {
    @Autowired
    private ApplicationUtils applicationUtils;
    
    @Autowired
    private CliFormatter formatter;

    @CliCommand(value = "list-commands", help = "List all commands")
    public String listCommands(
            @CliOption(key = { "container-name" }, mandatory = true, help = "Name of the container") String containerName) {
        List<Command> commands = applicationUtils.listCommands(containerName);
        return formatter.table(CommandTableColumn.values(), commands);
    }

    @CliCommand(value = "exec-command", help = "Execute a command")
    public String execCommands(
            @CliOption(key = { "container-name" }, mandatory = true, help = "Name of the container") String containerName,
            @CliOption(key = { "name" }, mandatory = true, help = "Name of the command") String name,
            @CliOption(key = {"arguments"}, mandatory = true, help = "Arguments for the command split by ','") String arguments) {
        applicationUtils.execCommand(name, containerName, arguments);
        
        return formatter.unlessQuiet(MessageFormat.format("Command {0} run", name));
    }
}
