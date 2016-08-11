package fr.treeptik.cloudunit.docker.model;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by nicolas on 10/08/2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Volume {

	@JsonProperty("Name")
	private String name;

	@JsonProperty("Label")
	private Map<String, String> labels;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Map<String, String> getLabels() {
		return labels;
	}

	public void setLabels(Map<String, String> labels) {
		this.labels = labels;
	}

}
