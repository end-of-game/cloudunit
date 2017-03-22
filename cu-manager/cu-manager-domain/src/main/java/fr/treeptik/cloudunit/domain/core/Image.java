package fr.treeptik.cloudunit.domain.core;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import fr.treeptik.cloudunit.orchestrator.core.ImageType;

public class Image {
    private static final String NAME_FORMAT = "(?:(?<namespace>[^/]+)/)?(?<basename>[^:]+)(?::(?<tag>.+))?";
    
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
    
    private String getField(String fieldName) {
        Matcher m = Pattern.compile(NAME_FORMAT).matcher(name);
        if (!m.matches()) {
            throw new IllegalStateException("Image name doesn't match the required pattern");
        }
        return m.group(fieldName);        
    }
    
    public String getNamespace() {
        return getField("namespace");
    }
    
    public String getBasename() {
        return getField("basename");
    }
    
    public String getTag() {
        return getField("tag");
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
