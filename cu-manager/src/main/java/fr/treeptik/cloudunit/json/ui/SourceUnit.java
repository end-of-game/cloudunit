package fr.treeptik.cloudunit.json.ui;

/**
 * Created by nicolas on 25/06/15.
 */
public class SourceUnit {

    private String name;

    public SourceUnit(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SourceUnit that = (SourceUnit) o;

        return !(name != null ? !name.equals(that.name) : that.name != null);

    }

    @Override
    public String toString() {
        return "SourceUnit{" +
                "name='" + name + '\'' +
                '}';
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }
}
