package fr.treeptik.cloudunit.orchestrator.core;

import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Container {
    private static final Logger LOGGER = LoggerFactory.getLogger(Container.class);
    
    private String id;
    private String name;
    private String imageName;
    private ContainerState state;
    private Map<String, Variable> variables;
    
    @Version
    private Long version;
    
    protected Container() {}
    
    public Container(String name, Image image) {
        this.name = name;
        this.imageName = image.getName();
        this.state = ContainerState.STOPPING;
        this.variables = new HashMap<>();
    }

    public String getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }

    public void setPending() {
        LOGGER.debug("set pending {}", state);
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
        return variables.entrySet().stream()
                .map(e -> String.format("%s=%s", e.getKey(), e.getValue().getValue()))
                .collect(Collectors.toList());
    }

    public Variable addVariable(String key, String value) {
        Variable variable = new Variable(key, value);
        variables.put(key, variable);
        return variable;
    }

    public void removeVariable(Variable variable) {
        variables.remove(variable.getKey());
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Container)) return false;
        Container other = (Container) obj;
        return new EqualsBuilder()
                .append(this.name, other.name)
                .isEquals();
    }
    
    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(name)
                .toHashCode();
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
