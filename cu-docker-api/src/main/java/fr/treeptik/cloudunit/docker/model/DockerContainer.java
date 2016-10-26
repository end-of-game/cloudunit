package fr.treeptik.cloudunit.docker.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by guillaume on 21/10/15.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DockerContainer implements Serializable {

	private static final long serialVersionUID = 1L;

	@JsonProperty("Config")
	private Config config;

	@JsonProperty("AppArmorProfile")
	private String appArmorProfile;

	@JsonProperty("Args")
	private List<String> args;

	@JsonProperty("Created")
	private String created;

	@JsonProperty("Driver")
	private String driver;

	@JsonProperty("ExecDriver")
	private String execDriver;

	@JsonProperty("ExecIDs")
	private List<String> execIDs;

	@JsonProperty("HostConfig")
	private HostConfig hostConfig;

	@JsonProperty("HostnamePath")
	private String hostnamePath;

	@JsonProperty("HostsPath")
	private String hostsPath;

	@JsonProperty("LogPath")
	private String logPath;

	@JsonProperty("Id")
	private String id;

	@JsonProperty("Image")
	private String image;

	@JsonProperty("MountLabel")
	private String mountLabel;

	@JsonProperty("Name")
	private String name;

	@JsonProperty("Path")
	private String path;

	@JsonProperty("ProcessLabel")
	private String processLabel;

	@JsonProperty("ResolvConfPath")
	private String resolvConfPath;

	@JsonProperty("RestartCount")
	private Long restartCount;

	@JsonProperty("Volumes")
	private Map<String, String> volumes;

	@JsonProperty("VolumesRW")
	private Map<String, String> volumesRW;

	@JsonProperty("State")
	private State state;

	@JsonProperty("NetworkSettings")
	private NetworkSettings networkSettings;

	@JsonProperty("Command")
	private String command;

	@JsonProperty("Labels")
	private Map<String, String> labels;

	@JsonProperty("Names")
	private List<String> names;

	@JsonProperty("Ports")
	private List<Object> ports;

	@JsonProperty("Status")
	private String status;

	@JsonProperty("GraphDriver")
	private Object graphDriver;

	@JsonProperty("Mounts")
	private List<Mounts> mounts;

	public List<Mounts> getMounts() {
		return mounts;
	}

	public void setMounts(List<Mounts> mounts) {
		this.mounts = mounts;
	}

	@JsonProperty("ImageID")
	private String ImageID;

	public Config getConfig() {
		return config;
	}

	public void setConfig(Config config) {
		this.config = config;
	}

	public String getAppArmorProfile() {
		return appArmorProfile;
	}

	public void setAppArmorProfile(String appArmorProfile) {
		this.appArmorProfile = appArmorProfile;
	}

	public List<String> getArgs() {
		return args;
	}

	public void setArgs(List<String> args) {
		this.args = args;
	}

	public String getCreated() {
		return created;
	}

	public void setCreated(String created) {
		this.created = created;
	}

	public String getDriver() {
		return driver;
	}

	public void setDriver(String driver) {
		this.driver = driver;
	}

	public String getExecDriver() {
		return execDriver;
	}

	public void setExecDriver(String execDriver) {
		this.execDriver = execDriver;
	}

	public List<String> getExecIDs() {
		return execIDs;
	}

	public void setExecIDs(List<String> execIDs) {
		this.execIDs = execIDs;
	}

	public HostConfig getHostConfig() {
		return hostConfig;
	}

	public void setHostConfig(HostConfig hostConfig) {
		this.hostConfig = hostConfig;
	}

	public String getHostnamePath() {
		return hostnamePath;
	}

	public void setHostnamePath(String hostnamePath) {
		this.hostnamePath = hostnamePath;
	}

	public String getHostsPath() {
		return hostsPath;
	}

	public void setHostsPath(String hostsPath) {
		this.hostsPath = hostsPath;
	}

	public String getLogPath() {
		return logPath;
	}

	public void setLogPath(String logPath) {
		this.logPath = logPath;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getMountLabel() {
		return mountLabel;
	}

	public void setMountLabel(String mountLabel) {
		this.mountLabel = mountLabel;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getProcessLabel() {
		return processLabel;
	}

	public void setProcessLabel(String processLabel) {
		this.processLabel = processLabel;
	}

	public String getResolvConfPath() {
		return resolvConfPath;
	}

	public void setResolvConfPath(String resolvConfPath) {
		this.resolvConfPath = resolvConfPath;
	}

	public Long getRestartCount() {
		return restartCount;
	}

	public void setRestartCount(Long restartCount) {
		this.restartCount = restartCount;
	}

	public Map<String, String> getVolumes() {
		return volumes;
	}

	public void setVolumes(Map<String, String> volumes) {
		this.volumes = volumes;
	}

	public Map<String, String> getVolumesRW() {
		return volumesRW;
	}

	public void setVolumesRW(Map<String, String> volumesRW) {
		this.volumesRW = volumesRW;
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	public NetworkSettings getNetworkSettings() {
		return networkSettings;
	}

	public void setNetworkSettings(NetworkSettings networkSettings) {
		this.networkSettings = networkSettings;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public Map<String, String> getLabels() {
		return labels;
	}

	public void setLabels(Map<String, String> labels) {
		this.labels = labels;
	}

	public List<String> getNames() {
		return names;
	}

	public void setNames(List<String> names) {
		this.names = names;
	}

	public List<Object> getPorts() {
		return ports;
	}

	public void setPorts(List<Object> ports) {
		this.ports = ports;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}
