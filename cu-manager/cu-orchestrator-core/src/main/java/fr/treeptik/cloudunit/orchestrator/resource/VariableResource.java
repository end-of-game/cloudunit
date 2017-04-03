package fr.treeptik.cloudunit.orchestrator.resource;

import org.springframework.hateoas.ResourceSupport;

import fr.treeptik.cloudunit.orchestrator.core.Variable;
import fr.treeptik.cloudunit.orchestrator.core.VariableRole;

public class VariableResource extends ResourceSupport {

    private String key;
    private String value;
    private VariableRole role;

    public VariableResource() {
    }

    public VariableResource(Variable variable) {
        this.key = variable.getKey();
        this.value = variable.getValue();
        this.role = variable.getRole();
    }

    public VariableResource(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
    
    public VariableRole getRole() {
        return role;
    }
    
    public void setRole(VariableRole role) {
        this.role = role;
    }
}
