package fr.treeptik.cloudunit.model;

import fr.treeptik.cloudunit.dto.EnvironmentVariableRequest;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Environment implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	private String keyEnv;

	private String valueEnv;

	@ManyToOne(fetch = FetchType.LAZY)
	private Application application;

	private String containerName;

	public Environment() {
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getKeyEnv() {
		return keyEnv;
	}

	public void setKeyEnv(String keyEnv) {
		this.keyEnv = keyEnv;
	}

	public String getValueEnv() {
		return valueEnv;
	}

	public void setValueEnv(String valueEnv) {
		this.valueEnv = valueEnv;
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

	public EnvironmentVariableRequest mapToRequest() {
		EnvironmentVariableRequest environmentVariableRequest = new EnvironmentVariableRequest();
		environmentVariableRequest.setId(id);
		environmentVariableRequest.setKey(keyEnv);
		environmentVariableRequest.setValue(valueEnv);
		return environmentVariableRequest;
	}

	@Override
	public String toString() {
		return "Environment{" + "id=" + id + ", key=" + keyEnv + ", value='" + valueEnv + '\'' + ", application="
				+ application + ", containerName=" + containerName + '}';
	}
}
