/*
 * LICENCE : CloudUnit is available under the Gnu Public License GPL V3 : https://www.gnu.org/licenses/gpl.txt
 *     but CloudUnit is licensed too under a standard commercial license.
 *     Please contact our sales team if you would like to discuss the specifics of our Enterprise license.
 *     If you are not sure whether the GPL is right for you,
 *     you can always test our software under the GPL and inspect the source code before you contact us
 *     about purchasing a commercial license.
 *
 *     LEGAL TERMS : "CloudUnit" is a registered trademark of Treeptik and can't be used to endorse
 *     or promote products derived from this project without prior written permission from Treeptik.
 *     Products or services derived from this software may not be called "CloudUnit"
 *     nor may "Treeptik" or similar confusing terms appear in their names without prior written permission.
 *     For any questions, contact us : contact@treeptik.fr
 */

package fr.treeptik.cloudunit.docker.model;

import fr.treeptik.cloudunit.docker.DockerContainerJSON;
import fr.treeptik.cloudunit.exception.DockerJSONException;
import fr.treeptik.cloudunit.utils.StaticSpringApplicationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DockerContainer {

	private static Logger logger = LoggerFactory
			.getLogger(DockerContainerJSON.class);

	private String id;
	private String name;
	private Long memory;
	private Long memorySwap;
	private String image;
	private String imageID;
	private String ip;
	private String state;
	private Map<String, String> ports;

	public List<String> getVolumesFrom() {
		return volumesFrom;
	}

	private Map<String, String> volumes;
	private List<String> volumesFrom;
	private List<Integer> portsToOpen;

	public void setVolumesFrom(List<String> volumesFrom) {
		this.volumesFrom = volumesFrom;
	}

	private List<String> links;
	private List<String> cmd;
	private Map<String, Map<String, String>[]> portBindings;

	private static DockerContainerJSON dockerContainerJSON = (DockerContainerJSON) StaticSpringApplicationContext
			.getBean("dockerContainerJSON");

	public DockerContainer() {

	}

	public static String checkDockerInfos(String hostAddress)
			throws DockerJSONException {
		String response = dockerContainerJSON.checkDockerInfos(hostAddress);

		return response;
	}

	public static DockerContainer findOne(DockerContainer dockerContainer,
			String hostIp) throws DockerJSONException {

		dockerContainer = dockerContainerJSON.findOne(
				dockerContainer.getName(), hostIp);
		return dockerContainer;
	}

	public static DockerContainer findOneWithImageID(
			DockerContainer dockerContainer, String hostIp)
			throws DockerJSONException {

		dockerContainer = dockerContainerJSON.findOneWithImageID(
				dockerContainer.getName(), hostIp);
		return dockerContainer;
	}

	public static String commit(DockerContainer dockerContainer, String tag,
			String hostIp, String repo) throws DockerJSONException {
		return dockerContainerJSON.commit(dockerContainer.getName(), tag,
				hostIp, repo);
	}

	public static void push(String image, String tag, String hostIp)
			throws DockerJSONException {
		dockerContainerJSON.push("localhost:5000/" + image + tag, tag, hostIp);
	}

	public static void pull(String image, String tag, String hostIp)
			throws DockerJSONException {
		dockerContainerJSON.pull("localhost:5000/" + image + tag, tag, hostIp);
	}

	public static void deleteImage(String id, String hostIp)
			throws DockerJSONException {
		dockerContainerJSON.deleteImage(id, hostIp);
	}

	public static void deleteImageIntoTheRegistry(String repository,
			String tag, String registryIP) throws DockerJSONException {
		dockerContainerJSON.deleteImageIntoTheRegistry(registryIP, tag,
				repository);
	}

	public static List<DockerContainer> listAllContainers(String hostAddress)
			throws DockerJSONException {

		List<DockerContainer> listContainers = dockerContainerJSON
				.listAllContainers(hostAddress);

		return listContainers;
	}

	public static DockerContainer create(DockerContainer dockerContainer,
			String hostIp) throws DockerJSONException {
		dockerContainer = dockerContainerJSON.create(dockerContainer, hostIp);
		return dockerContainer;
	}

	public static void remove(DockerContainer dockerContainer, String hostIp)
			throws DockerJSONException {

		dockerContainerJSON.remove(dockerContainer.getName(), hostIp);

		/**
		 * TODO : data temporaire
		 */
		if (dockerContainer.getImage().contains("mysql")
				|| dockerContainer.getImage().contains("postgres")
				|| dockerContainer.getImage().contains("mongo")
				|| dockerContainer.getImage().contains("redis")) {
			dockerContainerJSON.remove(dockerContainer.getName() + "-data",
					hostIp);
		}

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
				|| dockerContainer.getImage().contains("postgres")
				|| dockerContainer.getImage().contains("mongo")
				|| dockerContainer.getImage().contains("redis")) {
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
			portBindings = new HashMap<>();
		} else {
			portBindings = this.getPortBindings();
		}

		Map<String, String> mapHostIP = new HashMap<>();
		Map<String, String> mapHostPort = new HashMap<>();
		mapHostIP.put("HostIp", hostIP);
		mapHostPort.put("HostPort", hostPort);

		Map<String, String> portBindingsConf[] = (HashMap<String, String>[]) new HashMap[2];
		portBindingsConf[0] = mapHostIP;
		portBindingsConf[1] = mapHostPort;
		portBindings.put(containerPort_protocol, portBindingsConf);

		this.portBindings = portBindings;
	}

	public String getImageID() {
		return imageID;
	}

	public void setImageID(String imageID) {
		this.imageID = imageID;
	}

	public List<Integer> getPortsToOpen() {
		return portsToOpen;
	}

	public void setPortsToOpen(List<Integer> portsToOpen) {
		this.portsToOpen = portsToOpen;
	}
}
