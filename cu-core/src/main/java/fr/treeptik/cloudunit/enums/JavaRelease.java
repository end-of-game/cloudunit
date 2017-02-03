package fr.treeptik.cloudunit.enums;

/**
 * Created by nicolas on 10/06/2016.
 */
public enum JavaRelease {

    Java7("Java 7", "java7"),
    Java8("Java 8", "java8"),
    Java9("Java 9", "java9");

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
