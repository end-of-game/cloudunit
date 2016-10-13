package fr.treeptik.cloudunit.model;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

@Embeddable
public class VolumeAssociationId implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public VolumeAssociationId() {
	}

	public VolumeAssociationId(Server server, Volume volume) {
		this.server = server;
		this.volume = volume;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	private Server server;

	@ManyToOne(fetch = FetchType.LAZY)
	private Volume volume;

	public Server getServer() {
		return server;
	}

	public void setServer(Server server) {
		this.server = server;
	}

	public Volume getVolume() {
		return volume;
	}

	public void setVolume(Volume volume) {
		this.volume = volume;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((server == null) ? 0 : server.hashCode());
		result = prime * result + ((volume == null) ? 0 : volume.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		VolumeAssociationId other = (VolumeAssociationId) obj;
		if (server == null) {
			if (other.server != null)
				return false;
		} else if (!server.equals(other.server))
			return false;
		if (volume == null) {
			if (other.volume != null)
				return false;
		} else if (!volume.equals(other.volume))
			return false;
		return true;
	}

}
