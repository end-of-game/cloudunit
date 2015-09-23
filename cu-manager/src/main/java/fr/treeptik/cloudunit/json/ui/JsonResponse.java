package fr.treeptik.cloudunit.json.ui;

import java.io.Serializable;

public class JsonResponse implements Serializable {

    private int status;
    private String message = "";
    private String location = "";

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public JsonResponse(int status, String message, String location) {
        this.status = status;
        this.message = message;
        this.location = location;
    }
}