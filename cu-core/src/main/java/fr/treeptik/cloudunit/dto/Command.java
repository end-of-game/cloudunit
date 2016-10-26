package fr.treeptik.cloudunit.dto;

import java.util.List;

public class Command {

	private String name;

	private Integer argumentNumber;

	private List<String> arguments;

	public String getName() {
		return name;
	}

	public Integer getArgumentNumber() {
		return argumentNumber;
	}

	public List<String> getArguments() {
		return arguments;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setArgumentNumber(Integer argumentNumber) {
		this.argumentNumber = argumentNumber;
	}

	public Command() {
	}

	public Command(String name, Integer argumentNumber, List<String> arguments) {
		this.name = name;
		this.argumentNumber = argumentNumber;
		this.arguments = arguments;
	}

	public void setArguments(List<String> arguments) {
		this.arguments = arguments;
	}
}
