package fr.treeptik.cloudunit.docker.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

/**
 * Created by guillaume on 22/10/15.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Image implements Serializable {

	private static final long serialVersionUID = 1L;

	public void setName(String name) {
		this.name = name;
	}

	// Used for requestbody not the response
	@JsonIgnore
	private String name;

	@JsonProperty("Created")
	private String created;

	@JsonProperty("Container")
	private String container;

	@JsonProperty("Id")
	private String Id;

	@JsonProperty("Parent")
	private String sarent;

	@JsonProperty("Size")
	private String size;

	@JsonProperty("ContainerConfig")
	private Config containerConfig;

	@JsonProperty("Architecture")
	private String architecture;

	@JsonProperty("Author")
	private String author;

	@JsonProperty("Comment")
	private String comment;

	@JsonProperty("Config")
	private Config config;

	@JsonProperty("DockerVersion")
	private String dockerVersion;

	@JsonProperty("Os")
	private String os;

	@JsonProperty("VirtualSize")
	private Long virtualSize;

	@JsonProperty("RepoTags")
	private List<String> repoTags;

	public List<String> getRepoTags() {
		return repoTags;
	}

	public List<String> getRepoDigests() {
		return repoDigests;
	}

	public Object getGraphDriver() {
		return graphDriver;
	}

	@JsonProperty("RepoDigests")
	private List<String> repoDigests;

	@JsonProperty("GraphDriver")
	private Object graphDriver;

	public String getName() {
		return name;
	}

	public String getCreated() {
		return created;
	}

	public void setCreated(String created) {
		this.created = created;
	}

	public String getContainer() {
		return container;
	}

	public void setContainer(String container) {
		this.container = container;
	}

	public String getId() {
		return Id;
	}

	public void setId(String id) {
		Id = id;
	}

	public String getSarent() {
		return sarent;
	}

	public void setSarent(String sarent) {
		this.sarent = sarent;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public Config getContainerConfig() {
		return containerConfig;
	}

	public void setContainerConfig(Config containerConfig) {
		this.containerConfig = containerConfig;
	}

	public String getArchitecture() {
		return architecture;
	}

	public void setArchitecture(String architecture) {
		this.architecture = architecture;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Config getConfig() {
		return config;
	}

	public void setConfig(Config config) {
		this.config = config;
	}

	public String getDockerVersion() {
		return dockerVersion;
	}

	public void setDockerVersion(String dockerVersion) {
		this.dockerVersion = dockerVersion;
	}

	public String getOs() {
		return os;
	}

	public void setOs(String os) {
		this.os = os;
	}

	public Long getVirtualSize() {
		return virtualSize;
	}

	public void setVirtualSize(Long virtualSize) {
		this.virtualSize = virtualSize;
	}
}
