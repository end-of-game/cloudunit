package fr.treeptik.cloudunit.enums;

/**
 * Created by nicolas on 10/06/2016.
 */
public enum JavaRelease {

    Java7("Java JDK 7.0.55", "/opt/cloudunit/java/jdk1.7.0_55"),
    Java8("Java JDK 8.0.25", "/opt/cloudunit/java/jdk1.8.0_25");

    private final String label;
    private final String version;

    JavaRelease(String label, String version) {
        this.label = label;
        this.version = version;
    }

    public String getLabel() {
        return label;
    }

    public String getVersion() {
        return version;
    }

}
