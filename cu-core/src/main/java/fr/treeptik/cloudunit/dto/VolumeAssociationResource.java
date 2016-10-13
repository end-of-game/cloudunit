package fr.treeptik.cloudunit.dto;

import java.io.Serializable;

import fr.treeptik.cloudunit.model.VolumeAssociation;

public class VolumeAssociationResource implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String path;
	private String mode;
	private Integer volume;
	private String application;
	
	public VolumeAssociationResource() {}

	public VolumeAssociationResource(VolumeAssociation volumeAssociation) {
		this.path = volumeAssociation.getPath();
		this.mode = volumeAssociation.getMode();
		this.volume = volumeAssociation.getVolume().getId();
		this.application = volumeAssociation.getServer().getApplication().getName();
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
	public Integer getVolume() {
		return volume;
	}
	public void setVolume(Integer volume) {
		this.volume = volume;
	}
	public String getApplication() {
		return application;
	}
	public void setApplication(String application) {
		this.application = application;
	}
	
	@Override
	public String toString() {
		return "VolumeAssociationResource [path=" + path + ", mode=" + mode
				+ ", volumeId=" + volume + ", applicationName=" + application + "]";
	}
	
}
