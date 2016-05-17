package fr.treeptik.cloudunitmonitor.docker;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Inject;

import org.apache.http.client.utils.URIBuilder;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Component;

import fr.treeptik.cloudunitmonitor.conf.ApplicationEntryPoint;
import fr.treeptik.cloudunitmonitor.docker.model.DockerContainer;
import fr.treeptik.cloudunitmonitor.exception.DockerJSONException;
import fr.treeptik.cloudunitmonitor.exception.ErrorDockerJSONException;
import fr.treeptik.cloudunitmonitor.exception.FatalDockerJSONException;
import fr.treeptik.cloudunitmonitor.exception.WarningDockerJSONException;
import fr.treeptik.cloudunitmonitor.json.ui.JsonResponse;

@Component
@SuppressWarnings("unchecked")
public class DockerContainerJSON {

	private Logger logger = Logger.getLogger(DockerContainerJSON.class);

	private final String[] fixedPort = { "3306/tcp", "5432/tcp", "11211/tcp",
			"1521/tcp" };

	@Inject
	private JSONClient client;

	public DockerContainer findOne(String name, String hostIp)
			throws DockerJSONException {
		URI uri = null;
		DockerContainer dockerContainer = new DockerContainer();
		try {
			uri = new URIBuilder().setScheme(ApplicationEntryPoint.MODE).setHost(hostIp)
					.setPath("/containers/" + name + "/json").build();
			JsonResponse jsonResponse = null;

			// Docker error management

			try {

				jsonResponse = client.sendGet(uri);

				switch (jsonResponse.getStatus()) {
				case 404:
					throw new ErrorDockerJSONException(
							"docker : no such container");
				case 500:
					throw new ErrorDockerJSONException("docker : server error");
				}

			} catch (IOException e) {
				throw new FatalDockerJSONException(e.getLocalizedMessage());
			}

			// Init maps for volumes and ports

			Map<String, String> ports = new HashMap<>();
			Map<String, String> volumes = new HashMap<>();

			// SubString to remove the "/"
			String response = jsonResponse.getMessage();
			dockerContainer.setName(parser(response).get("Name").toString()
					.substring(1));
			dockerContainer.setId((String) parser(response).get("Id")
					.toString());

			Long memorySwap = (Long) parser(
					parser(response).get("Config").toString())
					.get("MemorySwap");
			Long memory = (Long) parser(
					parser(response).get("Config").toString()).get("Memory");
			dockerContainer.setMemorySwap(memorySwap);
			dockerContainer.setMemory(memory);
			dockerContainer.setImage((String) parser(
					parser(response).get("Config").toString()).get("Image"));

			if (dockerContainer.getImage().contains("mysql")) {

				if (parser(parser(response).get("HostConfig").toString()).get(
						"VolumesFrom") != null) {
					dockerContainer.setVolumesFrom(getList(
							parser(parser(response).get("HostConfig")
									.toString()), "VolumesFrom"));
				}
			}
			dockerContainer.setIp((String) parser(
					parser(response).get("NetworkSettings").toString()).get(
					"IPAddress"));

			if (parser(parser(response).get("NetworkSettings").toString()).get(
					"Ports") != null) {
				for (Object port : parser(
						parser(
								parser(response).get("NetworkSettings")
										.toString()).get("Ports").toString())
						.keySet()) {

					if (!Arrays.asList(fixedPort).contains(port.toString())) {
						Object forwardedPort = (Object) getObjectList(
								parser(parser(
										parser(response).get("NetworkSettings")
												.toString()).get("Ports")
										.toString()), port.toString()).get(0);
						ports.put(port.toString(),
								parser(forwardedPort.toString())
										.get("HostPort").toString());
					}
				}
			}

			if (parser(response).get("Volumes") != null) {
				for (Object volume : parser(
						parser(response).get("Volumes").toString()).keySet()) {

					volumes.put(volume.toString(),
							parser(parser(response).get("Volumes").toString())
									.get(volume.toString()).toString());

				}
			}

			dockerContainer.setVolumes(volumes);

			logger.debug("Volumes : " + volumes);

			dockerContainer.setPorts(ports);
			dockerContainer.setCmd(getList(parser(parser(response)
					.get("Config").toString()), "Cmd"));
			if (parser(parser(response).get("State").toString()).get("Running")
					.toString().equals("true")) {
				dockerContainer.setState("Running");
			} else {
				dockerContainer.setState("Paused");
			}

		} catch (NumberFormatException | URISyntaxException | ParseException e) {
			StringBuilder msgError = new StringBuilder(256);
			msgError.append(name).append(",hostIP=").append(hostIp)
					.append(",uri=").append(uri);
			logger.error(msgError, e);
			throw new FatalDockerJSONException("docker : error fatal");
		}

		return dockerContainer;
	}

	/**
	 * /containers/json : not same format as an inspect of container List all
	 * Running or Paused containers retrieve name (with cloudunit format), image
	 * and state
	 * 
	 * @param hostAddress
	 * @return
	 * @throws DockerJSONException
	 */
	public List<DockerContainer> listAllContainers(String hostAddress)
			throws DockerJSONException {

		URI uri = null;
		List<DockerContainer> listContainers = new ArrayList<>();
		try {

			uri = new URIBuilder().setScheme(ApplicationEntryPoint.MODE).setHost(hostAddress)
					.setPath("/containers/json").build();

			if (logger.isDebugEnabled()) {
				logger.debug("uri : " + uri);
			}

			JsonResponse jsonResponse;
			try {
				jsonResponse = client.sendGet(uri);
				switch (jsonResponse.getStatus()) {
				case 400:
					throw new ErrorDockerJSONException("docker : bad parameter");
				case 500:
					throw new ErrorDockerJSONException("docker : server error");
				}

			} catch (IOException e) {
				e.printStackTrace();
				throw new DockerJSONException("Error : listAllContainers "
						+ e.getLocalizedMessage(), e);
			}

			if (logger.isDebugEnabled()) {
				logger.debug("response : " + jsonResponse);
			}

			JSONParser jsonParser = new JSONParser();
			Object obj = jsonParser.parse(jsonResponse.getMessage());
			JSONArray array = (JSONArray) obj;
			for (int i = 0; i < array.size(); i++) {
				String containerDescription = array.get(i).toString();
				try {
					String firstSubString = (parser(containerDescription).get(
							"Names").toString()).substring(4);
					String Names = null;
					// for container with link where the link name is also show
					if (firstSubString.lastIndexOf(",") != -1) {
						Names = firstSubString.substring(0,
								firstSubString.lastIndexOf(",") - 1);

					} else {
						Names = firstSubString.substring(0,
								firstSubString.lastIndexOf("\""));
					}
					if (logger.isDebugEnabled()) {
						logger.debug("Names=[" + Names + "]");
					}
					if (Names != null && Names.trim().length() > 0) {
						DockerContainer dockerContainer = findOne(Names,
								hostAddress);
						if (dockerContainer != null) {
							listContainers.add(dockerContainer);
						}
					}
				} catch (ParseException e) {
					throw new DockerJSONException("Error : listAllContainers "
							+ e.getLocalizedMessage(), e);
				}
			}
		} catch (NumberFormatException | URISyntaxException | ParseException e) {
			StringBuilder msgError = new StringBuilder(256);
			msgError.append(",hostIP=").append(hostAddress).append(",uri=")
					.append(uri);
			logger.error(msgError, e);
			throw new FatalDockerJSONException("docker : error fatal");
		}
		return listContainers;
	}

	public DockerContainer start(DockerContainer dockerContainer, String hostIp)
			throws DockerJSONException {
		URI uri = null;
		try {
			uri = new URIBuilder()
					.setScheme(ApplicationEntryPoint.MODE)
					.setHost(hostIp)
					.setPath(
							"/containers/" + dockerContainer.getName()
									+ "/start").build();
			JSONObject config = new JSONObject();

			config.put("Privileged", Boolean.FALSE);
			config.put("PublishAllPorts", Boolean.TRUE);

			JSONArray link = new JSONArray();
			if (dockerContainer.getLinks() != null) {
				link.addAll(dockerContainer.getLinks());
				config.put("Links", link);
			}

			if (dockerContainer.getVolumes() != null) {
				for (String binds : dockerContainer.getVolumes().keySet()) {

					config.put("Binds", binds);
				}
			}

			if (dockerContainer.getVolumesFrom() != null) {
				JSONArray listVolumesFrom = new JSONArray();
				listVolumesFrom.addAll(dockerContainer.getVolumesFrom());

				config.put("VolumesFrom", listVolumesFrom);
			}

			/**
			 * Gestion du binding de port
			 */
			JSONObject portBindsConfigJSONFinal = new JSONObject();
			if (dockerContainer.getPortBindings() != null) {
				/**
				 * pour chaque ports à Binder (ex: 53/udp) on récupère le
				 * tableau avec les deux maps ex :- map1 = HostIp , 172.17.42.1
				 * - map2 = HostPort , 53
				 */
				for (Map.Entry<String, Map<String, String>[]> portKey : dockerContainer
						.getPortBindings().entrySet()) {

					logger.info("port/protocol to configure : "
							+ portKey.getKey());

					// On convertie le tableau en list pour itérer dessus
					List<Map<String, String>> listOfMapsConfig = Arrays
							.asList(portKey.getValue());

					JSONObject portConfigJSON = new JSONObject();
					for (Map<String, String> portConfigMap : listOfMapsConfig) {
						JSONArray portConfigJSONArray = new JSONArray();

						// transfert HostIP and HostPort avec leur valeurs dans
						// un JSONArray
						for (Entry<String, String> hostBindingMap : portConfigMap
								.entrySet()) {
							logger.info(hostBindingMap.getKey() + " : "
									+ hostBindingMap.getValue());

							portConfigJSON.put(hostBindingMap.getKey(),
									hostBindingMap.getValue());
							portConfigJSONArray.add(portConfigJSON);
						}
						portBindsConfigJSONFinal.put(portKey.getKey(),
								portConfigJSONArray);
						config.put("PortBindings", portBindsConfigJSONFinal);
					}

				}
			}

			int statusCode = client.sendPost(uri, config.toJSONString(),
					"application/json");

			switch (statusCode) {
			case 304:
				throw new WarningDockerJSONException(
						"container already started");
			case 404:
				throw new ErrorDockerJSONException("docker : no such container");
			case 500:
				throw new ErrorDockerJSONException("docker : error server");
			}
			dockerContainer = this.findOne(dockerContainer.getName(), hostIp);
		} catch (URISyntaxException | IOException e) {
			StringBuilder msgError = new StringBuilder(256);
			msgError.append(dockerContainer).append(",hostIP=").append(hostIp)
					.append(",uri=").append(uri);
			logger.error(msgError, e);
			throw new FatalDockerJSONException("docker : error fatal");
		}
		return dockerContainer;

	}

	public void stop(DockerContainer dockerContainer, String hostIp)
			throws DockerJSONException {
		URI uri = null;
		try {
			uri = new URIBuilder()
					.setScheme(ApplicationEntryPoint.MODE)
					.setHost(hostIp)
					.setPath(
							"/containers/" + dockerContainer.getName()
									+ "/stop").build();
			int statusCode = client.sendPost(uri, "", "application/json");
			switch (statusCode) {
			case 304:
				throw new WarningDockerJSONException(
						"container already stopped");
			case 404:
				throw new ErrorDockerJSONException("docker : no such container");
			case 500:
				throw new ErrorDockerJSONException("docker : server error");
			}

		} catch (URISyntaxException | IOException e) {
			StringBuilder msgError = new StringBuilder(256);
			msgError.append(dockerContainer).append(",hostIP=").append(hostIp)
					.append(",uri=").append(uri);
			logger.error(msgError, e);
			throw new FatalDockerJSONException("docker : error fatal");
		}
	}

	private static JSONObject parser(String message) throws ParseException {
		JSONParser jsonParser = new JSONParser();
		JSONObject json = (JSONObject) jsonParser.parse(message);
		return json;
	}

	private static List<String> getList(JSONObject object, String listName) {
		List<String> list = new ArrayList<>();
		JSONArray node = (JSONArray) object.get(listName);
		Iterator<String> iterator = node.iterator();
		while (iterator.hasNext()) {
			list.add(iterator.next());
		}
		return list;
	}

	private static List<Object> getObjectList(JSONObject object, String listName) {
		List<Object> list = new ArrayList<>();
		JSONArray node = (JSONArray) object.get(listName);
		Iterator<Object> iterator = node.iterator();
		while (iterator.hasNext()) {
			list.add(iterator.next());
		}
		return list;
	}

}
