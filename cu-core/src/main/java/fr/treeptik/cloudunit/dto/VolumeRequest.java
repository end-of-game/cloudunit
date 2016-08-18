package fr.treeptik.cloudunit.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import fr.treeptik.cloudunit.model.Volume;

@JsonIgnoreProperties(ignoreUnknown = true)
public class VolumeRequest {

	private Integer id;

	private String name;

	private String path;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Volume mapToVolumeRequest() {
		Volume volume = new Volume();
		volume.setId(id);
		volume.setName(name);
		volume.setPath(path);
		return volume;
	}

}
