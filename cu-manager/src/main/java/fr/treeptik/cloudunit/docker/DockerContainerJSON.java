/*
 * LICENCE : CloudUnit is available under the GNU Affero General Public License : https://gnu.org/licenses/agpl.html
 * but CloudUnit is licensed too under a standard commercial license.
 * Please contact our sales team if you would like to discuss the specifics of our Enterprise license.
 * If you are not sure whether the AGPL is right for you,
 * you can always test our software under the AGPL and inspect the source code before you contact us
 * about purchasing a commercial license.
 *
 * LEGAL TERMS : "CloudUnit" is a registered trademark of Treeptik and can't be used to endorse
 * or promote products derived from this project without prior written permission from Treeptik.
 * Products or services derived from this software may not be called "CloudUnit"
 * nor may "Treeptik" or similar confusing terms appear in their names without prior written permission.
 * For any questions, contact us : contact@treeptik.fr
 */


package fr.treeptik.cloudunit.docker;

import fr.treeptik.cloudunit.docker.model.DockerContainer;
import fr.treeptik.cloudunit.dto.JsonResponse;
import fr.treeptik.cloudunit.exception.DockerJSONException;
import fr.treeptik.cloudunit.exception.ErrorDockerJSONException;
import fr.treeptik.cloudunit.exception.FatalDockerJSONException;
import fr.treeptik.cloudunit.exception.WarningDockerJSONException;
import org.apache.http.client.utils.URIBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;

@Component
@SuppressWarnings("unchecked")
public class DockerContainerJSON {

    private final static String[] fixedPort = {"3306/tcp", "5432/tcp", "11211/tcp",
            "1521/tcp", "27017/tcp"};

    private static Logger logger = LoggerFactory.getLogger(DockerContainerJSON.class);

    @Inject
    private JSONClient client;

    @Value("${docker.endpoint.mode}")
    private String dockerEndpointMode;

    private boolean isHttpMode;

    private static String CU_KVM = System.getenv().get("CU_KVM");
    private static String CU_DEV_RANDOM_POLICY = System.getenv().get("CU_DEV_RANDOM_POLICY");

    @PostConstruct
    public void initDockerEndPointMode() {
        if ("http".equalsIgnoreCase(dockerEndpointMode)) {
            logger.warn("Docker TLS mode is disabled");
            isHttpMode = true;
        } else {
            isHttpMode = false;
        }
    }

    private static JSONObject parser(String message)
            throws ParseException {
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

    public DockerContainer findOne(String name, String hostIp)
            throws DockerJSONException {
        URI uri = null;
        DockerContainer dockerContainer = new DockerContainer();
        try {
            uri = new URIBuilder().setScheme(dockerEndpointMode).setHost(hostIp)
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

            if (parser(parser(response).get("HostConfig").toString()).get(
                    "VolumesFrom") != null) {
                dockerContainer.setVolumesFrom(getList(parser(parser(response)
                                .get("HostConfig").toString()),
                        "VolumesFrom"));
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
                                        .toString()),
                                port.toString()).get(0);
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
            }

            if (parser(parser(response).get("State").toString()).get("Running")
                    .toString().equals("false")
                    && parser(parser(response).get("State").toString())
                    .get("Paused").toString().equals("false")) {
                dockerContainer.setState("Paused");
            }

            if (parser(parser(response).get("State").toString()).get("Paused")
                    .toString().equals("true")) {
                dockerContainer.setState("Paused");
            }

        } catch (NumberFormatException | URISyntaxException | ParseException e) {
            StringBuilder msgError = new StringBuilder(256);
            msgError.append(name).append(",hostIP=").append(hostIp)
                    .append(",uri=").append(uri);
            logger.error("" + msgError, e);
            throw new FatalDockerJSONException("docker : error fatal");
        }

        return dockerContainer;
    }

    public DockerContainer findOneWithImageID(String name, String hostIp)
            throws DockerJSONException {
        URI uri = null;
        DockerContainer dockerContainer = new DockerContainer();
        try {
            uri = new URIBuilder().setScheme(dockerEndpointMode).setHost(hostIp)
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

            dockerContainer.setImageID((String) parser(response).get("Image")
                    .toString());
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

            if (parser(parser(response).get("HostConfig").toString()).get(
                    "VolumesFrom") != null) {
                dockerContainer.setVolumesFrom(getList(parser(parser(response)
                                .get("HostConfig").toString()),
                        "VolumesFrom"));
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
                                        .toString()),
                                port.toString()).get(0);
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
            }

            if (parser(parser(response).get("State").toString()).get("Running")
                    .toString().equals("false")
                    && parser(parser(response).get("State").toString())
                    .get("Paused").toString().equals("false")) {
                dockerContainer.setState("Paused");
            }

            if (parser(parser(response).get("State").toString()).get("Paused")
                    .toString().equals("true")) {
                dockerContainer.setState("Paused");
            }

        } catch (NumberFormatException | URISyntaxException | ParseException e) {
            StringBuilder msgError = new StringBuilder(256);
            msgError.append(name).append(",hostIP=").append(hostIp)
                    .append(",uri=").append(uri);
            logger.error("" + msgError, e);
            throw new FatalDockerJSONException("docker : error fatal");
        }

        return dockerContainer;
    }

    public String checkDockerInfos(String hostAddress)
            throws DockerJSONException {
        URI uri = null;
        JsonResponse jsonResponse;
        try {
            uri = new URIBuilder().setScheme(dockerEndpointMode).setHost(hostAddress)
                    .setPath("/info").build();

            jsonResponse = client.sendGet(uri);
            if (jsonResponse.getStatus() == 500) {
                throw new ErrorDockerJSONException("server error");
            }

        } catch (NumberFormatException | URISyntaxException | IOException e) {

            throw new FatalDockerJSONException("docker : error fatal");
        }
        return jsonResponse.getMessage();
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

            uri = new URIBuilder().setScheme(dockerEndpointMode).setHost(hostAddress)
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
                    if (Names.trim().length() > 0) {
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
            logger.error("" + msgError, e);
            throw new FatalDockerJSONException("docker : error fatal");
        }
        return listContainers;
    }

    public DockerContainer create(DockerContainer dockerContainer, String hostIp)
            throws DockerJSONException {
        URI uri = null;
        try {
            uri = new URIBuilder().setScheme(dockerEndpointMode).setHost(hostIp)
                    .setPath("/containers/create")
                    .setParameter("name", dockerContainer.getName()).build();

            JSONObject config = new JSONObject();
            config.put("AttachStdin", Boolean.FALSE);
            config.put("AttachStdout", Boolean.TRUE);
            config.put("AttachStderr", Boolean.TRUE);
            config.put("Memory", dockerContainer.getMemory());
            config.put("MemorySwap", dockerContainer.getMemorySwap());
            config.put("Image", dockerContainer.getImage());
            config.put("ExposedPorts", dockerContainer.getPorts());

            try {
                JSONObject hostConfig = new JSONObject();
                JSONArray listVolumesFrom = new JSONArray();
                if (dockerContainer.getVolumesFrom() != null) {
                    for (int i = 0, iMax = dockerContainer.getVolumesFrom()
                            .size(); i < iMax; i++) {
                        listVolumesFrom.add(dockerContainer.getVolumesFrom()
                                .get(i));
                    }
                    hostConfig.put("VolumesFrom", listVolumesFrom);
                    config.put("HostConfig", hostConfig);
                }
            } catch (Exception ex) {
                logger.error("" + dockerContainer.getVolumesFrom(), ex);
            }

            JSONArray listCmd = new JSONArray();
            listCmd.addAll(dockerContainer.getCmd());
            config.put("Cmd", listCmd);
            int statusCode = client.sendPost(uri, config.toJSONString(),
                    "application/json");
            switch (statusCode) {
                case 404:
                    throw new ErrorDockerJSONException(
                            "Image or container not found");
                case 406:
                    throw new ErrorDockerJSONException(
                            "impossible to attach (container not running)");
                case 500:
                    throw new ErrorDockerJSONException("server error");
            }

        } catch (URISyntaxException | IOException e) {
            StringBuilder msgError = new StringBuilder(256);
            msgError.append(dockerContainer).append(",hostIP=").append(hostIp)
                    .append(",uri=").append(uri);
            System.out.println(msgError);
            logger.error("" + msgError, e);
            throw new FatalDockerJSONException("docker : error fatal");
        }

        return dockerContainer;
    }

    public void remove(String name, String hostIp)
            throws DockerJSONException {
        URI uri = null;

        if (logger.isInfoEnabled()) {
            logger.info("container name : " + name);
        }
        try {
            //this.kill(name, hostIp);
            uri = new URIBuilder().setScheme(dockerEndpointMode).setHost(hostIp)
                    .setPath("/containers/" + name).setParameter("v", "1").setParameter("force",
                            "true")
                    .build();
            if (logger.isInfoEnabled()) {
                logger.info("URI DELETE =  " + uri);
            }

            int statusCode = client.sendDelete(uri);
            switch (statusCode) {
                case 400:
                    throw new ErrorDockerJSONException("docker : bad parameter");
                case 404:
                    throw new ErrorDockerJSONException("docker : no such container");
                case 500: {
                    // TODO : il faut comprendre le pourquoi de l'erreur
                    StringBuilder msgError = new StringBuilder(256);
                    msgError.append("name=").append(name).append(", hostIp=")
                            .append(hostIp);
                    logger.error("docker : server error for removing " + msgError);
                    break;
                }
            }

        } catch (URISyntaxException | IOException e) {
            StringBuilder msgError = new StringBuilder(256);
            msgError.append(name).append(",hostIP=").append(hostIp)
                    .append(",uri=").append(uri);
            logger.error("" + msgError, e);
            throw new FatalDockerJSONException("docker : error fatal");
        }
    }

    // methodes d'appels pour les backups

    public DockerContainer start(DockerContainer dockerContainer, String hostIp)
            throws DockerJSONException {
        URI uri = null;
        try {
            uri = new URIBuilder()
                    .setScheme(dockerEndpointMode)
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

            if (dockerContainer.getVolumesFrom() != null) {
                JSONArray listVolumesFrom = new JSONArray();
                if (dockerContainer.getVolumesFrom() != null) {
                    for (int i = 0, iMax = dockerContainer.getVolumesFrom()
                            .size(); i < iMax; i++) {
                        listVolumesFrom.add(dockerContainer.getVolumesFrom()
                                .get(i));
                    }
                }
                config.put("VolumesFrom", listVolumesFrom);
            }

            // ajout des volumes provenant de l'hote
            final List volumes = new ArrayList<String>() {
                {
                    add("/etc/localtime:/etc/localtime:ro");
                    add("/etc/timezone:/etc/timezone:ro");
                }
            };

            BooleanSupplier isRunningIntoKVM = () -> "true".equalsIgnoreCase(CU_KVM);
            String cuKvmDevRandomPolicy = "/dev/urandom:/dev/random";
            if (CU_DEV_RANDOM_POLICY != null) {
                cuKvmDevRandomPolicy = CU_DEV_RANDOM_POLICY;
                logger.debug("Force cuKvmDevRandomPolicy to : " + cuKvmDevRandomPolicy);
            }
            if (isRunningIntoKVM.getAsBoolean()) {
                volumes.add(cuKvmDevRandomPolicy);
            }

            config.put("Binds", volumes);

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

            int statusCode = client.sendPostForStart(uri,
                    config.toJSONString(), "application/json");

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
            logger.error("" + msgError, e);
            throw new FatalDockerJSONException("docker : error fatal");
        }
        return dockerContainer;

    }

    public void stop(DockerContainer dockerContainer, String hostIp)
            throws DockerJSONException {
        URI uri = null;
        try {
            uri = new URIBuilder()
                    .setScheme(dockerEndpointMode)
                    .setHost(hostIp)
                    .setPath(
                            "/containers/" + dockerContainer.getName()
                                    + "/stop").setParameter("t", "5").build();
            logger.info(uri.toString());
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
            logger.error("" + msgError, e);
            throw new FatalDockerJSONException("docker : error fatal");
        }
    }

    public void kill(String name, String hostIp)
            throws DockerJSONException {
        URI uri = null;
        try {
            uri = new URIBuilder().setScheme(dockerEndpointMode).setHost(hostIp)
                    .setPath("/containers/" + name + "/kill").build();
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
            msgError.append(name).append(",hostIP=").append(hostIp)
                    .append(",uri=").append(uri);
            logger.error(msgError.toString(), e);
            throw new FatalDockerJSONException("docker : error fatal");
        }
    }

    public String commit(String name, String tag, String hostIp, String repo)
            throws DockerJSONException {
        URI uri = null;
        Map<String, Object> response = null;
        try {
            uri = new URIBuilder().setScheme(dockerEndpointMode).setHost(hostIp)
                    .setPath("/commit").setParameter("container", name)
                    .setParameter("tag", tag)
                    .setParameter("repo", repo)
                    .build();
            response = client
                    .sendPostAndGetImageID(uri, "", "application/json");
            int statusCode = (int) response.get("code");
            switch (statusCode) {
                case 304:
                    throw new WarningDockerJSONException(
                            "container already stopped");
                case 404:
                    throw new ErrorDockerJSONException("docker : no such container");
                case 500:
                    throw new ErrorDockerJSONException("docker : server error");
            }
        } catch (URISyntaxException | WarningDockerJSONException
                | ErrorDockerJSONException | IOException e) {
            StringBuilder msgError = new StringBuilder(256);
            msgError.append(name).append(",hostIP=").append(hostIp)
                    .append(",uri=").append(uri);
            logger.error(msgError.toString(), e);
            throw new FatalDockerJSONException("docker : error fatal");
        }

        return (String) response.get("body");
    }

    public String push(String name, String tag, String hostIp)
            throws DockerJSONException {
        URI uri = null;
        Map<String, Object> response = null;
        try {
            uri = new URIBuilder().setScheme(dockerEndpointMode).setHost(hostIp)
                    .setPath("/images/" + name.toLowerCase() + "/push")
                    .setParameter("tag", tag.toLowerCase()).build();
            response = client.sendPostWithRegistryHost(uri, "",
                    "application/json");
            System.out.println(response);
            int statusCode = (int) response.get("code");

            switch (statusCode) {
                case 304:
                    throw new WarningDockerJSONException(
                            "container already stopped");
                case 404:
                    throw new ErrorDockerJSONException("docker : no such container");
                case 500:
                    throw new ErrorDockerJSONException("docker : server error");
            }
        } catch (URISyntaxException | WarningDockerJSONException
                | ErrorDockerJSONException | IOException e) {
            StringBuilder msgError = new StringBuilder(256);
            msgError.append(name.toLowerCase()).append(",hostIP=")
                    .append(hostIp).append(",uri=").append(uri);
            logger.error(msgError.toString(), e);
            throw new FatalDockerJSONException("docker : error fatal");
        }

        logger.info((String) response.get("body"));
        String digest = "";
        int indexOfDigest = ((String) response.get("body")).indexOf("Digest:");
        if (indexOfDigest != -1) {
            digest = ((String) response.get("body")).substring(indexOfDigest+8);
            digest = digest.substring(0, digest.length()-4);
        }
        return digest;

    }

    public String pull(String name, String tag, String hostIp)
            throws DockerJSONException {
        URI uri = null;
        Map<String, Object> response = null;
        try {
            uri = new URIBuilder().setScheme(dockerEndpointMode).setHost(hostIp)
                    .setPath("/images/create")
                    .setParameter("fromImage", name.toLowerCase())
                    .setParameter("tag", tag.toLowerCase()).build();
            response = client.sendPostWithRegistryHost(uri, "",
                    "application/json");
            int statusCode = (int) response.get("code");

            switch (statusCode) {
                case 304:
                    throw new WarningDockerJSONException(
                            "container already stopped");
                case 404:
                    throw new ErrorDockerJSONException("docker : no such container");
                case 500:
                    throw new ErrorDockerJSONException("docker : server error");
            }
        } catch (URISyntaxException | WarningDockerJSONException
                | ErrorDockerJSONException | IOException e) {
            StringBuilder msgError = new StringBuilder(256);
            msgError.append(name.toLowerCase()).append(",hostIP=")
                    .append(hostIp).append(",uri=").append(uri);
            logger.error(msgError.toString(), e);
            throw new FatalDockerJSONException("docker : error fatal");
        }

        logger.info((String) response.get("body"));

        return (String) response.get("body");

    }

    public void deleteImage(String id, String hostIp)
            throws DockerJSONException {
        URI uri = null;
        try {
            uri = new URIBuilder().setScheme(dockerEndpointMode).setHost(hostIp)
                    .setPath("/images/" + id).build();
            int statusCode = client.sendDelete(uri);
            switch (statusCode) {
                case 304:
                    throw new WarningDockerJSONException(
                            "container already stopped");
                case 404:
                    throw new ErrorDockerJSONException("docker : no such container");
                case 500:
                    throw new ErrorDockerJSONException("docker : server error");
            }
        } catch (URISyntaxException | WarningDockerJSONException
                | ErrorDockerJSONException | IOException e) {
            StringBuilder msgError = new StringBuilder(512);
            msgError.append(id).append(",hostIP=").append(hostIp)
                    .append(",uri=").append(uri);
            logger.error(msgError.toString(), e);
            throw new FatalDockerJSONException("docker : error fatal");
        }
    }


}
