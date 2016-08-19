package fr.treeptik.cloudunit.model;

import fr.treeptik.cloudunit.dto.CommandRequest;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
public class Command implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	private String value;

	private String description;

	@ElementCollection
	private List<String> arguments;

	private String containerId;

	@ManyToOne
	private Image image;

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

	public String getDescription() { return description; }

	public void setDescription(String description) { this.description = description; }

	public List<String> getArguments() {
		return arguments;
	}

	public void setArguments(List<String> arguments) {
		this.arguments = arguments;
	}

	public String getContainerId() {
		return containerId;
	}

	public void setContainerId(String containerId) {
		this.containerId = containerId;
	}

	public Image getImage() { return image; }

	public void setImage(Image image) { this.image = image; }

	public CommandRequest mapToRequest() {
		CommandRequest commandRequest = new CommandRequest();
		commandRequest.setId(id);
		commandRequest.setArguments(arguments);
		commandRequest.setValue(value);
		commandRequest.setDescription(description);
		commandRequest.setContainerId(containerId);
		return commandRequest;
	}
}
