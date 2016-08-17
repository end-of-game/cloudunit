package fr.treeptik.cloudunit.dto;

/**
 * Created by nicolas on 09/06/2016.
 */
public final class Script {

    private String title;
    private String content;

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    @Override
    public String toString() {
        return "Script{" +
                "title='" + title + '\'' +
                '}';
    }
}
