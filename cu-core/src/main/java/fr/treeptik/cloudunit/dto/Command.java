package fr.treeptik.cloudunit.dto;

import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FilenameUtils;

public class Command {
	private String name;

	private String path;
	
	private List<String> argumentNames;

	protected Command() {}

	public Command(String path, List<String> argumentNames) {
		this.name = FilenameUtils.getBaseName(path);
		this.path = path;
		this.argumentNames = argumentNames;
	}

    public String getName() {
        return name;
    }
    
    public String getPath() {
        return path;
    }

	public List<String> getArgumentNames() {
        return Collections.unmodifiableList(argumentNames);
    }
	
	public void addArgumentName(String argumentName) {
	    argumentNames.add(argumentName);
	}
	
	public String getCommandLine(List<String> arguments) {
	    return String.format("%s %s", path, String.join(" ", arguments));
	}
}
