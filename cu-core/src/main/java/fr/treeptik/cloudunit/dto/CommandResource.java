package fr.treeptik.cloudunit.dto;

import java.util.ArrayList;
import java.util.List;

import org.springframework.hateoas.ResourceSupport;

public class CommandResource extends ResourceSupport {
    private String name;
    private List<String> argumentNames;
    
    public CommandResource() {}
    
    public CommandResource(Command command) {
        this.name = command.getName();
        this.argumentNames = new ArrayList<>(command.getArgumentNames());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getArgumentNames() {
        return argumentNames;
    }

    public void setArgumentNames(List<String> argumentNames) {
        this.argumentNames = argumentNames;
    }
}
