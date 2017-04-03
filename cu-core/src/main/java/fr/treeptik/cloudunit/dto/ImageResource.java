package fr.treeptik.cloudunit.dto;

import fr.treeptik.cloudunit.model.Image;

import java.io.Serializable;

/**
 * Created by gborg on 06/02/17.
 */
public class ImageResource implements Serializable {

    private Integer id;
    private String name;

    private String path;

    private String displayName;

    private Integer status;

    private String imageType;

    private String managerName;

    private String prefixEnv;

    private boolean isEnable;

    public void setName(String name) {
        this.name = name;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public void setImageType(String imageType) {
        this.imageType = imageType;
    }

    public void setManagerName(String managerName) {
        this.managerName = managerName;
    }

    public void setPrefixEnv(String prefixEnv) {
        this.prefixEnv = prefixEnv;
    }

    public void setEnable(boolean enable) {
        isEnable = enable;
    }

    public void setPull(boolean pull) {
        isPull = pull;
    }

    public String getName() {

        return name;
    }

    public String getPath() {
        return path;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Integer getStatus() {
        return status;
    }

    public String getImageType() {
        return imageType;
    }

    public String getManagerName() {
        return managerName;
    }

    public String getPrefixEnv() {
        return prefixEnv;
    }

    public boolean isEnable() {
        return isEnable;
    }

    public boolean isPull() {
        return isPull;
    }

    private boolean  isPull;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }


    public ImageResource() {
    }

    public ImageResource(Image image) {
        this.id = image.getId();
        this.name = image.getName();
        this.displayName = image.getDisplayName();
        this.imageType = image.getImageType();
        this.managerName = image.getManagerName();
        this.path = image.getPath();
        this.isEnable = image.isEnable();
        this.status = image.getStatus();
        this.imageType = image.getImageType();
        this.prefixEnv = image.getPrefixEnv();
    }
}
