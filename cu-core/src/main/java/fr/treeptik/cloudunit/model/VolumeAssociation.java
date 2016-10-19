package fr.treeptik.cloudunit.model;

import java.io.Serializable;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class VolumeAssociation implements Serializable {

	private static final long serialVersionUID = 1L;

	@JsonIgnore
	@EmbeddedId
	private VolumeAssociationId volumeAssociationId;

	private String path;

	private String mode;

	public VolumeAssociation() {
	}

	public VolumeAssociation(VolumeAssociationId volumeAssociationId, String path, String mode) {
		this.volumeAssociationId = volumeAssociationId;
		this.path = path;
		this.mode = mode;
	}

	public VolumeAssociationId getVolumeAssociationId() {
		return volumeAssociationId;
	}


	public Server getServer() {
		return volumeAssociationId.getServer();
	}

	public Volume getVolume() {
		return volumeAssociationId.getVolume();
	}
	
	public void setVolumeAssociationId(VolumeAssociationId volumeAssociationId) {
		this.volumeAssociationId = volumeAssociationId;
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

}
