package fr.treeptik.cloudunit.dto;

import fr.treeptik.cloudunit.enums.RemoteExecAction;

public class Hook {

    private String containerName;

    private RemoteExecAction remoteExecAction;

    public Hook(String containerName, RemoteExecAction remoteExecAction) {
        this.remoteExecAction = remoteExecAction;
        this.containerName = containerName;
    }

    public String getContainerName() {
        return containerName;
    }

    public void setContainerName(String containerName) {
        this.containerName = containerName;
    }

    public RemoteExecAction getRemoteExecAction() {
        return remoteExecAction;
    }

    public void setRemoteExecAction(RemoteExecAction remoteExecAction) {
        this.remoteExecAction = remoteExecAction;
    }

}
