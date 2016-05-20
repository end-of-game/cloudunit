package fr.treeptik.cloudunit.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nicolas on 20/05/2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ImageList {

    private List<Image> images = new ArrayList<>();

    public List<Image> getImages() {
        return images;
    }

    public void add(String name, boolean favorite) {
        Image image = new Image(name, favorite);
        images.add(image);
    }
}

class Image {

    private String name;
    private boolean favorite;

    public Image(String name, boolean favorite) {
        this.name = name;
        this.favorite = favorite;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Image image = (Image) o;

        if (favorite != image.favorite) return false;
        return name.equals(image.name);

    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + (favorite ? 1 : 0);
        return result;
    }

}
