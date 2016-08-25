package fr.treeptik.cloudunit.dto;

import java.util.List;

public class Command {

	private String name;

	private Integer argumentNumber;

	private List<String> arguments;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getArgumentNumber() {
		return argumentNumber;
	}

	public void setArgumentNumber(Integer argumentNumber) {
		this.argumentNumber = argumentNumber;
	}

	public List<String> getArguments() {
		return arguments;
	}

	public void setArguments(List<String> arguments) {
		this.arguments = arguments;
	}
}
