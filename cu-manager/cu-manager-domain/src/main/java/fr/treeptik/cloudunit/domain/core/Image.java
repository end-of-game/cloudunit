package fr.treeptik.cloudunit.domain.core;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import fr.treeptik.cloudunit.orchestrator.core.ImageType;

public class Image {
    private String id;
    private String name;
    private ImageType type;
    
    protected Image() {}
    
    public Image(String name, ImageType type) {
        this.name = name;
        this.type = type;
    }
    
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
    
    public String getBasename() {
        if (name.contains("/")) {
            return name.split("/", 2)[1];
        } else {
            return name;
        }
    }
    
    public ImageType getType() {
        return type;
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
