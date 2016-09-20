package fr.treeptik.cloudunit.dto;

/**
 * Created by nicolas on 06/06/2016.
 */
public class EnvUnit {

    private String key;
    private String value;

    public EnvUnit(String key, String value) {
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

    @Override
    public String toString() {
        return "EnvUnit{" +
                "key='" + key + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
