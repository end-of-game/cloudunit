package fr.treeptik.cloudunit.orchestrator.core;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class Variable {
    public static Variable assign(Container container, Variable variable) {
        if (variable.role != null) {
            return new Variable(variable.key, variable.role.generateValue(container), variable.role);
        }
        return null;
    }
    
    private String key;
    private String value;
    private VariableRole role;

    public Variable() {}

    public Variable(String key, String value) {
        this(key, value, null);
    }
    
    public Variable(String key, String value, VariableRole role) {
        this.key = key;
        this.value = value;
        this.role = role;
    }

    public String getKey() {
        return key;
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
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Variable)) return false;
        
        Variable other = (Variable) obj;
        return new EqualsBuilder()
                .append(this.key, other.key)
                .isEquals();
    }
    
    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(key)
                .toHashCode();
    }
    
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
