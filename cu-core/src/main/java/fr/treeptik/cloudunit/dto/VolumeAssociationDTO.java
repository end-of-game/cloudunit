package fr.treeptik.cloudunit.dto;

import java.io.Serializable;

public class VolumeAssociationDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private String containerName;

	private String path;

	private String mode;

	private String volumeName;

	private String applicationName;

	public String getContainerName() {
		return containerName;
	}

	public void setContainerName(String containerName) {
		this.containerName = containerName;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public String getVolumeName() {
		return volumeName;
	}

	public void setVolumeName(String volumeName) {
		this.volumeName = volumeName;
	}

	@Override
	public String toString() {
		return "VolumeAssociationDTO [containerName=" + containerName + ", path=" + path + ", mode=" + mode
				+ ", volumeName=" + volumeName + "]";
	}

	public String getApplicationName() {
		return applicationName;
	}

	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}

}
