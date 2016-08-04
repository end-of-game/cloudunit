package fr.treeptik.cloudunit.docker.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by guillaume on 21/10/15.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Config implements Serializable {

	private static final long serialVersionUID = 1L;

	@JsonProperty("AttachStdin")
	private Boolean attachStdin;

	@JsonProperty("AttachStdout")
	private Boolean attachStdout;

	@JsonProperty("AttachStderr")
	private Boolean attachStderr;

	@JsonProperty("Memory")
	private Long memory;

	@JsonProperty("MemorySwap")
	private Long memorySwap;

	@JsonProperty("Image")
	private String image;

	@JsonProperty("Cmd")
	private List<String> cmd;

	@JsonProperty("ExposedPorts")
	private Map<String, Object> exposedPorts;

	@JsonProperty("HostConfig")
	private HostConfig hostConfig;

	@JsonProperty("CpuShares")
	private Long cpuShares;

	@JsonProperty("Cpuset")
	private String cpuset;

	@JsonProperty("Domainname")
	private String domainname;

	@JsonProperty("Entrypoint")
	private List<String> entrypoint;

	@JsonProperty("Env")
	private List<String> env;

	@JsonProperty("Hostname")
	private String hostname;

	@JsonProperty("Labels")
	private Map<String, String> labels;

	@JsonProperty("MacAddress")
	private String macAddress;

	@JsonProperty("NetworkDisabled")
	private Boolean networkDisabled;

	@JsonProperty("OnBuild")
	private String onBuild;

	@JsonProperty("OpenStdin")
	private Boolean openStdin;

	@JsonProperty("PortSpecs")
	private String portSpecs;

	@JsonProperty("StdinOnce")
	private Boolean stdinOnce;

	@JsonProperty("Tty")
	private Boolean tty;

	@JsonProperty("User")
	private String user;

	@JsonProperty("Volumes")
	private Map<String, Map<String, String>> volumes;

	@JsonProperty("WorkingDir")
	private String workingDir;

	public Boolean getAttachStdin() {
		return attachStdin;
	}

	public void setAttachStdin(Boolean attachStdin) {
		this.attachStdin = attachStdin;
	}

	public Boolean getAttachStdout() {
		return attachStdout;
	}

	public void setAttachStdout(Boolean attachStdout) {
		this.attachStdout = attachStdout;
	}

	public Boolean getAttachStderr() {
		return attachStderr;
	}

	public void setAttachStderr(Boolean attachStderr) {
		this.attachStderr = attachStderr;
	}

	public Long getMemory() {
		return memory;
	}

	public void setMemory(Long memory) {
		this.memory = memory;
	}

	public Long getMemorySwap() {
		return memorySwap;
	}

	public void setMemorySwap(Long memorySwap) {
		this.memorySwap = memorySwap;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public List<String> getCmd() {
		return cmd;
	}

	public void setCmd(List<String> cmd) {
		this.cmd = cmd;
	}

	public Map<String, Object> getExposedPorts() {
		return exposedPorts;
	}

	public void setExposedPorts(Map<String, Object> exposedPorts) {
		this.exposedPorts = exposedPorts;
	}

	public HostConfig getHostConfig() {
		return hostConfig;
	}

	public void setHostConfig(HostConfig hostConfig) {
		this.hostConfig = hostConfig;
	}

	public Long getCpuShares() {
		return cpuShares;
	}

	public void setCpuShares(Long cpuShares) {
		this.cpuShares = cpuShares;
	}

	public String getCpuset() {
		return cpuset;
	}

	public void setCpuset(String cpuset) {
		this.cpuset = cpuset;
	}

	public String getDomainname() {
		return domainname;
	}

	public void setDomainname(String domainname) {
		this.domainname = domainname;
	}

	public List<String> getEntrypoint() {
		return entrypoint;
	}

	public void setEntrypoint(List<String> entrypoint) {
		this.entrypoint = entrypoint;
	}

	public List<String> getEnv() {
		return env;
	}

	public void setEnv(List<String> env) {
		this.env = env;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public Map<String, String> getLabels() {
		return labels;
	}

	public void setLabels(Map<String, String> labels) {
		this.labels = labels;
	}

	public String getMacAddress() {
		return macAddress;
	}

	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}

	public Boolean getNetworkDisabled() {
		return networkDisabled;
	}

	public void setNetworkDisabled(Boolean networkDisabled) {
		this.networkDisabled = networkDisabled;
	}

	public String getOnBuild() {
		return onBuild;
	}

	public void setOnBuild(String onBuild) {
		this.onBuild = onBuild;
	}

	public Boolean getOpenStdin() {
		return openStdin;
	}

	public void setOpenStdin(Boolean openStdin) {
		this.openStdin = openStdin;
	}

	public String getPortSpecs() {
		return portSpecs;
	}

	public void setPortSpecs(String portSpecs) {
		this.portSpecs = portSpecs;
	}

	public Boolean getStdinOnce() {
		return stdinOnce;
	}

	public void setStdinOnce(Boolean stdinOnce) {
		this.stdinOnce = stdinOnce;
	}

	public Boolean getTty() {
		return tty;
	}

	public void setTty(Boolean tty) {
		this.tty = tty;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public Map<String, Map<String, String>> getVolumes() {
		return volumes;
	}

	public void setVolumes(Map<String, Map<String, String>> volumes) {
		this.volumes = volumes;
	}

	public String getWorkingDir() {
		return workingDir;
	}

	public void setWorkingDir(String workingDir) {
		this.workingDir = workingDir;
	}
}
