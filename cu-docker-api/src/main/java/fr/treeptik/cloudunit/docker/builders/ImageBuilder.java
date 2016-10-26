package fr.treeptik.cloudunit.docker.builders;

import fr.treeptik.cloudunit.docker.model.Config;
import fr.treeptik.cloudunit.docker.model.Image;

/**
 * Created by guillaume on 22/10/15.
 */
public class ImageBuilder {
    private String name;
    private String created;
    private String container;
    private String Id;
    private String sarent;
    private String size;
    private Config containerConfig;

    private ImageBuilder() {
    }

    public static ImageBuilder anImage() {
        return new ImageBuilder();
    }

    public ImageBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public ImageBuilder withCreated(String created) {
        this.created = created;
        return this;
    }

    public ImageBuilder withContainer(String container) {
        this.container = container;
        return this;
    }

    public ImageBuilder withId(String Id) {
        this.Id = Id;
        return this;
    }

    public ImageBuilder withSarent(String sarent) {
        this.sarent = sarent;
        return this;
    }

    public ImageBuilder withSize(String size) {
        this.size = size;
        return this;
    }

    public ImageBuilder withContainerConfig(Config containerConfig) {
        this.containerConfig = containerConfig;
        return this;
    }

    public ImageBuilder but() {
        return anImage().withName(name).withCreated(created).withContainer(container).withId(Id).withSarent(sarent).withSize(size).withContainerConfig(containerConfig);
    }

    public Image build() {
        Image image = new Image();
        image.setName(name);
        image.setCreated(created);
        image.setContainer(container);
        image.setId(Id);
        image.setSarent(sarent);
        image.setSize(size);
        image.setContainerConfig(containerConfig);
        return image;
    }
}
