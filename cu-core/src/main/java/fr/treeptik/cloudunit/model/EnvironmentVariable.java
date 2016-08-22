package fr.treeptik.cloudunit.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class EnvironmentVariable implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	private String key;

	private String value;

	@ManyToOne(fetch = FetchType.LAZY)
	@JsonIgnore
	private Application application;

	@JsonIgnore
	private String containerName;

	public EnvironmentVariable() {
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Application getApplication() {
		return application;
	}

	public void setApplication(Application application) {
		this.application = application;
	}

	public String getContainerName() {
		return containerName;
	}

	public void setContainerName(String containerName) {
		this.containerName = containerName;
	}

	@Override
	public String toString() {
		return "EnvironmentVariable [id=" + id + ", key=" + key + ", value=" + value + ", containerName="
				+ containerName + "]";
	}

}
