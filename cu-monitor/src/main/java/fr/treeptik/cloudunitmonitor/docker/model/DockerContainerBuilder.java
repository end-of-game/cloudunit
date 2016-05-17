package fr.treeptik.cloudunitmonitor.docker.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DockerContainerBuilder extends
		DockerContainerBuilderBase<DockerContainerBuilder> {
	public static DockerContainerBuilder dockerContainer() {
		return new DockerContainerBuilder();
	}

	public DockerContainerBuilder() {
		super(new DockerContainer());
	}

	public DockerContainer build() {
		return getInstance();
	}
}

class DockerContainerBuilderBase<GeneratorT extends DockerContainerBuilderBase<GeneratorT>> {
	private DockerContainer instance;

	protected DockerContainerBuilderBase(DockerContainer aInstance) {
		instance = aInstance;
	}

	protected DockerContainer getInstance() {
		return instance;
	}

	@SuppressWarnings("unchecked")
	public GeneratorT withName(String aValue) {
		instance.setName(aValue);

		return (GeneratorT) this;
	}

	@SuppressWarnings("unchecked")
	public GeneratorT withImage(String aValue) {
		instance.setImage(aValue);

		return (GeneratorT) this;
	}

	@SuppressWarnings("unchecked")
	public GeneratorT withPorts(Map<String, String> aValue) {
		instance.setPorts(aValue);

		return (GeneratorT) this;
	}

	@SuppressWarnings("unchecked")
	public GeneratorT withLinks(List<String> aValue) {
		instance.setLinks(aValue);

		return (GeneratorT) this;
	}

	@SuppressWarnings("unchecked")
	public GeneratorT withAddedLink(String aValue) {
		if (instance.getLinks() == null) {
			instance.setLinks(new ArrayList<String>());
		}

		((ArrayList<String>) instance.getLinks()).add(aValue);

		return (GeneratorT) this;
	}

	@SuppressWarnings("unchecked")
	public GeneratorT withCmd(List<String> aValue) {
		instance.setCmd(aValue);

		return (GeneratorT) this;
	}

	@SuppressWarnings("unchecked")
	public GeneratorT withAddedCmdElement(String aValue) {
		if (instance.getCmd() == null) {
			instance.setCmd(new ArrayList<String>());
		}

		((ArrayList<String>) instance.getCmd()).add(aValue);

		return (GeneratorT) this;
	}

	@SuppressWarnings("unchecked")
	public GeneratorT withMemory(Long aValue) {
		instance.setMemory(aValue);

		return (GeneratorT) this;
	}

	@SuppressWarnings("unchecked")
	public GeneratorT withMemorySwap(Long aValue) {
		instance.setMemorySwap(aValue);

		return (GeneratorT) this;
	}

	@SuppressWarnings("unchecked")
	public GeneratorT withId(String aValue) {
		instance.setId(aValue);

		return (GeneratorT) this;
	}

	@SuppressWarnings("unchecked")
	public GeneratorT withIp(String aValue) {
		instance.setIp(aValue);

		return (GeneratorT) this;
	}
}
