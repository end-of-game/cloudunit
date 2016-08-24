package fr.treeptik.cloudunit.model;

import java.io.Serializable;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

@Entity
public class VolumeAssociation implements Serializable {

	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private VolumeAssociationId volumeAssociationId;

	public VolumeAssociationId getVolumeAssociationId() {
		return volumeAssociationId;
	}

	public void setVolumeAssociationId(VolumeAssociationId volumeAssociationId) {
		this.volumeAssociationId = volumeAssociationId;
	}

}
