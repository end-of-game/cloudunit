package fr.treeptik.cloudunit.orchestrator.core;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
    
    /**
     * Mounted volumes.
     * 
     * Each key is a volume name, and each value is a mount point.
     * Consequence: a volume cannot be mounted twice on the same container,
     * however two volumes can be mounted on the same path.
     */
    private Map<String, Mount> mounts;
    
    @Version
    private Long version;
    
    protected Container() {}
    
    public Container(String name, Image image) {
        this.name = name;
        this.imageName = image.getRepositoryTag();
        this.state = ContainerState.STOPPING;
        this.variables = image.getVariables().stream()
                .map(v -> Variable.assign(this, v))
                .collect(Collectors.toMap(v -> v.getKey(), v -> v));
        this.mounts = new HashMap<>();
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
    
    public String getLocalDnsName() {
        return name;
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
    
    public Collection<Mount> getMounts() {
        return Collections.unmodifiableCollection(mounts.values());
    }
    
    public boolean isMounted(Volume volume) {
        return isMounted(volume.getName());
    }

    public boolean isMounted(String volumeName) {
        return mounts.containsKey(volumeName);
    }
    
    public Optional<Mount> getMount(Volume volume) {
        return getMount(volume.getName());
    }

    public Optional<Mount> getMount(String volumeName) {
        return Optional.ofNullable(mounts.get(volumeName));
    }
    
    /**
     * Mount a volume on this container.
     * 
     * @param volume  the name of the volume
     * @param mountPoint  the path on which to mount it
     * 
     * @throws IllegalArgumentException if {@code volume} has already been mounted.
     * @throws NullPointerException if {@code volume} or {@code mountPoint} is {@literal null}.
     */
    public Mount addMount(Volume volume, String mountPoint) {
        if (volume == null || mountPoint == null) {
            throw new NullPointerException("volumeName and mountPoint must not be null");
        }
        if (isMounted(volume)) {
            throw new IllegalArgumentException("This volume has already been mounted");
        }
        
        Mount mount = new Mount(volume.getName(), mountPoint);
        mounts.put(volume.getName(), mount);
        return mount;
    }
    
    public void removeMount(Volume volume) {
        removeMount(volume.getName());
    }
    
    public void removeMount(String volumeName) {
        mounts.remove(volumeName);
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
