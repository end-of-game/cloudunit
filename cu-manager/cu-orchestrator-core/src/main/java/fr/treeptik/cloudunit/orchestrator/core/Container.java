package fr.treeptik.cloudunit.orchestrator.core;

import java.util.*;
import java.util.stream.Collectors;

public class Container {
    private String id;
    private String containerId;
    private String name;
    private String imageName;
    private ContainerState state;
    private Map<String, Variable> variables;
    
    protected Container() {}
    
    public Container(String name, Image image) {
        this.name = name;
        this.imageName = image.getName();
        this.state = ContainerState.STOPPED;
        this.variables = new HashMap<>();
    }

    public String getId() {
        return id;
    }
    
    public String getContainerId() {
        return containerId;
    }
    
    public void setContainerId(String containerId) {
        this.containerId = containerId;
    }
    
    public String getName() {
        return name;
    }

    public void setPending() {
        state = state.getPendingState();
    }
    public ContainerState getState() {
        return state;
    }

    public void setState(ContainerState state) {
        this.state = state;
    }
    
    public String getImageName() {
        return imageName;
    }

    public Optional<Variable> getVariable(String key) {
        return Optional.ofNullable(variables.get(key));
    }

    public Collection<Variable> getVariables() {
        return Collections.unmodifiableCollection(variables.values());
    }

    public List<String> getVariablesAsList() {
        return variables.entrySet().stream().map(e ->  e.getKey() + "=" + e.getValue().getValue()).collect(Collectors.toList());
    }

    public Variable addVariable(String key, String value) {
        Variable variable = new Variable(key, value);
        variables.put(key, variable);
        return variable;
    }

}
