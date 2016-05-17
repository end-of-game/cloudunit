package fr.treeptik.cloudunitmonitor.docker.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import fr.treeptik.cloudunitmonitor.docker.DockerContainerJSON;
import fr.treeptik.cloudunitmonitor.exception.DockerJSONException;
import fr.treeptik.cloudunitmonitor.utils.StaticSpringApplicationContext;

public class DockerContainer {

	private static Logger logger = Logger.getLogger(DockerContainerJSON.class);

	private String id;
	private String name;
	private Long memory;
	private Long memorySwap;
	private String image;
	private String ip;
	private String state;
	private Map<String, String> ports;
	private Map<String, String> volumes;
	private List<String> volumesFrom;
	private List<String> links;
	private List<String> cmd;
	private Map<String, Map<String, String>[]> portBindings;

	private static DockerContainerJSON dockerContainerJSON = (DockerContainerJSON) StaticSpringApplicationContext
			.getBean("dockerContainerJSON");

	public DockerContainer() {

	}

	public static DockerContainer findOne(DockerContainer dockerContainer,
			String hostIp) throws DockerJSONException {

		dockerContainer = dockerContainerJSON.findOne(
				dockerContainer.getName(), hostIp);
		return dockerContainer;
	}

	public static List<DockerContainer> listAllContainers(String hostAddress)
			throws DockerJSONException {

		List<DockerContainer> listContainers = dockerContainerJSON
				.listAllContainers(hostAddress);

		return listContainers;
	}

	public static DockerContainer start(DockerContainer dockerContainer,
			String hostIp) throws DockerJSONException {
		dockerContainer = dockerContainerJSON.start(dockerContainer, hostIp);
		return dockerContainer;
	}

	public static void stop(DockerContainer dockerContainer, String hostIp)
			throws DockerJSONException {

		// Stop du container application
		logger.debug("Stop dockerContainer : " + dockerContainer);
		dockerContainerJSON.stop(dockerContainer, hostIp);

		// Stop du container de données associés
		if (dockerContainer.getImage().contains("mysql")
				|| dockerContainer.getImage().contains("postgresql")
				|| dockerContainer.getImage().contains("git")) {
			logger.debug("Stop docker DataContainer : " + dockerContainer);
			dockerContainer = dockerContainerJSON.findOne(
					dockerContainer.getName() + "-data", hostIp);
			dockerContainerJSON.stop(dockerContainer, hostIp);
		}
	}

	public String getName() {
		return name;
	}

	public String getImage() {
		return image;
	}

	public Map<String, String> getPorts() {
		return ports;
	}

	public List<String> getLinks() {
		return links;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public void setPorts(Map<String, String> ports) {
		this.ports = ports;
	}

	public void setLinks(List<String> links) {
		this.links = links;
	}

	public List<String> getCmd() {
		return cmd;
	}

	public void setCmd(List<String> cmd) {
		this.cmd = cmd;
	}

	public Long getMemory() {
		return memory;
	}

	public Long getMemorySwap() {
		return memorySwap;
	}

	public void setMemory(Long memory) {
		this.memory = memory;
	}

	public void setMemorySwap(Long memorySwap) {
		this.memorySwap = memorySwap;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public Map<String, String> getVolumes() {
		return volumes;
	}

	public void setVolumes(Map<String, String> volumes) {
		this.volumes = volumes;
	}

	public List<String> getVolumesFrom() {
		return volumesFrom;
	}

	public void setVolumesFrom(List<String> volumesFrom) {
		this.volumesFrom = volumesFrom;
	}

	@Override
	public String toString() {
		return "DockerContainer [id=" + id + ", name=" + name + ", memory="
				+ memory + ", memorySwap=" + memorySwap + ", image=" + image
				+ ", ip=" + ip + ", state=" + state + ", ports=" + ports
				+ ", volumes=" + volumes + ", volumesFrom=" + volumesFrom
				+ ", links=" + links + ", cmd=" + cmd + "]";
	}

	public Map<String, Map<String, String>[]> getPortBindings() {
		return portBindings;
	}

	/**
	 * 
	 * @param containerPort_protocol
	 * @param hostIP
	 * @param hostPort
	 */
	@SuppressWarnings("unchecked")
	public void setPortBindings(String containerPort_protocol, String hostIP,
			String hostPort) {
		Map<String, Map<String, String>[]> portBindings;

		if (this.getPortBindings() == null) {
			portBindings = new HashMap<String, Map<String, String>[]>();
		} else {
			portBindings = this.getPortBindings();
		}

		Map<String, String> mapHostIP = new HashMap<String, String>();
		Map<String, String> mapHostPort = new HashMap<String, String>();
		mapHostIP.put("HostIp", hostIP);
		mapHostPort.put("HostPort", hostPort);

		Map<String, String> portBindingsConf[] = (HashMap<String, String>[]) new HashMap[2];
		portBindingsConf[0] = mapHostIP;
		portBindingsConf[1] = mapHostPort;
		portBindings.put(containerPort_protocol, portBindingsConf);

		this.portBindings = portBindings;
	}
}
