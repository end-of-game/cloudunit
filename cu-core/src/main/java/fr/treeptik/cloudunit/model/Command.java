package fr.treeptik.cloudunit.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

@Entity
public class Command implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	private String value;

	@ElementCollection
	private Set<String> arguments;

	private String containerId;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Set<String> getArguments() {
		return arguments;
	}

	public void setArguments(Set<String> arguments) {
		this.arguments = arguments;
	}

	public String getContainerId() {
		return containerId;
	}

	public void setContainerId(String containerId) {
		this.containerId = containerId;
	}
}
