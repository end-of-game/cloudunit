package fr.treeptik.cloudunit.orchestrator.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class Image {    
    public static class Builder {
        private final String name;
        private final ImageType type;
        private final String repositoryTag;
        private final String serviceName;
        private final String version;
        private String displayName;
        private List<Variable> variables = new ArrayList<>();
        
        private Builder(String serviceName, String version, ImageType type, String repositoryTag) {
            this.serviceName = serviceName;
            this.name = String.format("%s:%s", serviceName, version);
            this.displayName = String.format("%s %s", serviceName, version);
            this.type = type;
            this.version = version;
            this.repositoryTag = repositoryTag;
        }

        public Builder displayName(String displayName) {
            this.displayName = displayName;
            return this;
        }

        public Builder variable(Variable variable) {
            this.variables.add(variable);
            return this;
        }
        
        public Image build() {
            return new Image(this);
        }
    }
    
    public static Builder of(String serviceName, String version, ImageType type, String repositoryTag) {
        return new Builder(serviceName, version, type, repositoryTag);
    }
    
    private String id;
    private String name;
    private ImageType type;
    private String serviceName;
    private String version;
    private String displayName;
    private Map<String, Variable> variables;
    private String repositoryTag;
    
    public Image() {}
    
    private Image(Builder builder) {
        this.name = builder.name;
        this.displayName = builder.displayName;
        this.type = builder.type;
        this.serviceName = builder.serviceName;
        this.version = builder.version;
        this.variables = builder.variables.stream()
                .collect(Collectors.toMap(v -> v.getKey(), v -> v));
        this.repositoryTag = builder.repositoryTag;
    }

    public String getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getServiceName() {
        return serviceName;
    }
    
    public String getVersion() {
        return version;
    }
    
    public ImageType getType() {
        return type;
    }
    
    public Collection<Variable> getVariables() {
        return Collections.unmodifiableCollection(variables.values());
    }

    public Optional<Variable> getVariable(String key) {
        return Optional.ofNullable(variables.get(key));
    }
    
    public String getRepositoryTag() {
        return repositoryTag;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Image)) return false;
        
        Image other = (Image) obj;
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
