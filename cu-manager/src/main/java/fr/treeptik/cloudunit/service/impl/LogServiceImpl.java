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


package fr.treeptik.cloudunit.service.impl;

import fr.treeptik.cloudunit.docker.model.DockerContainer;
import fr.treeptik.cloudunit.dto.LogUnit;
import fr.treeptik.cloudunit.exception.ErrorDockerJSONException;
import fr.treeptik.cloudunit.exception.ServiceException;
import fr.treeptik.cloudunit.model.Application;
import fr.treeptik.cloudunit.model.Module;
import fr.treeptik.cloudunit.model.Server;
import fr.treeptik.cloudunit.model.Status;
import fr.treeptik.cloudunit.service.ApplicationService;
import fr.treeptik.cloudunit.service.LogService;
import fr.treeptik.cloudunit.utils.AuthentificationUtils;
import fr.treeptik.cloudunit.utils.ContainerMapper;
import org.elasticsearch.action.deletebyquery.DeleteByQueryResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by nicolas on 25/08/2014.
 */
@Service
public class LogServiceImpl
    implements LogService {

    private static final Integer DEFAULT_ES_PORT = 9300;

    // Dictionnaire pour mettre en relation une application avec un ou plusieurs
    // volumes
    private static ConcurrentHashMap<String, Set<String>> volumesByContainer = new ConcurrentHashMap<>();

    private Logger logger = LoggerFactory.getLogger(LogServiceImpl.class);

    @Inject
    private ContainerMapper containerMapper;

    @Inject
    private ApplicationService applicationService;

    @Inject
    private AuthentificationUtils authentificationUtils;

    @Value("${elasticsearch.ip}")
    private String esIp;

    @Override
    public List<LogUnit> listByApp(String applicationName, String containerId,
                                   String source, Integer nbRows)
        throws ServiceException {
        List<LogUnit> lines = new ArrayList<>();
        try {
            Application application = applicationService.findByNameAndUser(
                authentificationUtils.getAuthentificatedUser(),
                applicationName);

            Set<String> directories = volumesByContainer.get(containerId);
            if (logger.isDebugEnabled()) {
                logger.debug("" + directories);
            }
            lines = gatherRowsFromES(esIp, directories, source, nbRows);

        } catch (Exception e) {
            StringBuilder msgError = new StringBuilder(512);
            msgError.append("applicationName=").append(applicationName)
                .append(", ");
            msgError.append("containerId=").append(containerId).append(", ");
            msgError.append("source=").append(source).append(", ");
            msgError.append("nbRows=").append(nbRows).append(", ");
            throw new ServiceException(msgError.toString(), e);
        }
        return lines;
    }

    @Override
    public int deleteLogsForApplication(String applicationName)
        throws ServiceException {
        List<LogUnit> lines = new ArrayList<>();
        try {
            Application application = applicationService.findByNameAndUser(
                authentificationUtils.getAuthentificatedUser(),
                applicationName);

            List<String> listContainersId = applicationService
                .listContainersId(applicationName);
            for (String containerId : listContainersId) {
                Set<String> directories = volumesByContainer.get(containerId);
                if (directories != null) {
                    Iterator<String> iterDir = directories.iterator();
                    while (iterDir.hasNext()) {
                        String directory = iterDir.next();
                        deleteRowsIntoES(esIp, directory);
                    }
                }
            }

        } catch (Exception e) {
            StringBuilder msgError = new StringBuilder(512);
            msgError.append("applicationName=").append(applicationName)
                .append(", ");
            throw new ServiceException(msgError.toString(), e);
        }
        return 0;
    }

    public void deleteRowsIntoES(String ipES, String directory) {
        TransportClient client = new TransportClient();

        try {
            client.addTransportAddress(new InetSocketTransportAddress(ipES,
                DEFAULT_ES_PORT));

            SearchRequestBuilder searchRequestBuilder = client.prepareSearch();
            searchRequestBuilder.setSearchType(SearchType.DFS_QUERY_THEN_FETCH);
            searchRequestBuilder.addSort("@timestamp", SortOrder.DESC);

            DeleteByQueryResponse response = client
                .prepareDeleteByQuery("cloudunit")
                .setQuery(QueryBuilders.matchQuery("path", directory))
                .execute().actionGet();

            if (logger.isDebugEnabled()) {
                logger.debug("" + response);
            }

        } catch (Exception e) {
            StringBuilder msgError = new StringBuilder(512);
            msgError.append("ipES=").append(ipES).append(", ");
            msgError.append("directory=").append(directory).append(", ");
            logger.error(msgError.toString(), e);
        } finally {
            client.close();
        }
    }

    /**
     * @param ipES
     * @param directories
     * @param nbRows
     */
    private List<LogUnit> gatherRowsFromES(String ipES,
                                           Set<String> directories, String source, Integer nbRows) {

        if (logger.isDebugEnabled()) {
            logger.debug("IP ES :" + ipES);
            logger.debug("" + directories);
            logger.debug("source : " + source);
            logger.debug("nbRows : " + nbRows);
        }
        List<LogUnit> lines = new ArrayList<LogUnit>();

        // Si aucun répertoire, on retourne zero lignes de logs
        if (directories == null) {
            return lines;
        }

        TransportClient client = new TransportClient();
        try {
            client.addTransportAddress(new InetSocketTransportAddress(ipES,
                DEFAULT_ES_PORT));

            SearchRequestBuilder searchRequestBuilder = client.prepareSearch();
            searchRequestBuilder.setSearchType(SearchType.DFS_QUERY_THEN_FETCH);
            searchRequestBuilder.addSort("@timestamp", SortOrder.DESC);
            searchRequestBuilder.setSize(nbRows);

            Iterator myIter = directories.iterator();
            while (myIter.hasNext()) {
                String directory = (String) myIter.next();
                logger.debug(directory);
                directory = directory.substring(directory.lastIndexOf("/") + 1);
                // warning : this is a double constraint. Not an erreur about
                // path
                searchRequestBuilder.setQuery(QueryBuilders.termQuery("path",
                    directory));
                searchRequestBuilder.setQuery(QueryBuilders.termQuery("path",
                    source));
            }

            SearchResponse response = searchRequestBuilder.execute()
                .actionGet();
            SearchHit[] hits = response.getHits().getHits();
            if (hits != null) {
                for (int i = 0, iMax = hits.length; i < iMax; i++) {
                    SearchHit oneHit = hits[i];
                    Map<String, Object> map = oneHit.sourceAsMap();
                    String message = (String) map.get("message");
                    String pathFile = (String) map.get("path");
                    String timestamp = (String) map.get("@timestamp");
                    lines.add(new LogUnit(pathFile, timestamp, message));
                }
            }

        } catch (Exception e) {
            StringBuilder msgError = new StringBuilder(256);
            msgError.append("ipES=").append(ipES);
            msgError.append(", containersDir=").append(directories);
            msgError.append(", nbRows=").append(nbRows);
            logger.error(msgError.toString(), e);
        } finally {
            if (client != null) {
                client.close();
            }
        }

        return lines;

    }

    /**
     * Methode qui permet de mettre en relation les applications avec les
     * volumes de logs
     */
    @Scheduled(fixedDelay = 10000)
    public void generateRelationBetweenApplicationAndVolumes() {
        try {
            if ("true".equals(System.getenv("CU_MAINTENANCE"))) {
                return;
            }
            List<Application> applications = applicationService.findAll();
            if (applications != null) {
                for (Application application : applications) {
                    // On ne scrute que les applications qui sont démarrées
                    if (Status.START.equals(application.getStatus()) == false)
                        continue;
                    try {
                        // Serveurs
                        List<Server> servers = application.getServers();
                        for (Server server : servers) {
                            DockerContainer dockerContainer = new DockerContainer();
                            dockerContainer.setName(server.getName());
                            dockerContainer.setImage(server.getImage()
                                .getName());
                            dockerContainer = DockerContainer.findOne(
                                dockerContainer,
                                application.getManagerIp());
                            server = containerMapper
                                .mapDockerContainerToServer(
                                    dockerContainer, server);
                            Map<String, String> volumes = server.getVolumes();
                            String clefApplication = server.getContainerID();
                            addMountDirectoryForAnApplication(clefApplication,
                                volumes);
                        }
                        // Modules
                        List<Module> modules = application.getModules();
                        for (Module module : modules) {
                            DockerContainer dockerContainer = new DockerContainer();
                            dockerContainer.setName(module.getName());
                            dockerContainer.setImage(module.getImage()
                                .getName());
                            dockerContainer = DockerContainer.findOne(
                                dockerContainer,
                                application.getManagerIp());
                            module = containerMapper
                                .mapDockerContainerToModule(
                                    dockerContainer, module);
                            Map<String, String> volumes = module.getVolumes();
                            String clefApplication = module.getContainerID();
                            addMountDirectoryForAnApplication(clefApplication,
                                volumes);
                        }
                    } catch (ErrorDockerJSONException ex) {
                        // TODO : refactoriser le code pour extraire le message
                        if (!"docker : no such container".equalsIgnoreCase(ex.getMessage())) {
                            logger.error(application.toString(), ex);
                        }
                    } catch (Exception ex) {
                        StringBuilder msgError = new StringBuilder(512);
                        msgError.append(application);
                        // Si une application sort en erreur, il ne faut pas
                        // arrêter la suite des traitements
                        logger.error(msgError.toString(), ex);
                    }
                }
            }
        } catch (Exception e) {
            // On catch l'exception car traitement en background.
            logger.error(e.getMessage());
        }
    }

    /**
     * Ajoute pour une application donné (identifié par nom d'application +
     * login utilisateur) le chemin d'une répertoire à une liste.
     * Exemple de structure [demo1-nicolas] --> [/var/lib/docker/vfs/aaa,
     * /var/lib/docker/vfs/bbb, /var/lib/docker/vfs/ccc]} [mysql1-nicolas] -->
     * [/var/lib/docker/vfs/ddd, /var/lib/docker/vfs/eee,
     * /var/lib/docker/vfs/fff]}
     *
     * @param key
     * @param volumes
     */
    private void addMountDirectoryForAnApplication(String key,
                                                   Map<String, String> volumes) {
        // Le paramètre volumes represente les données issues de la BDD (mapping
        // jpa)
        if (volumes != null) {
            Iterator myIter = volumes.keySet().iterator();
            while (myIter.hasNext()) {
                String directoryInTheContainer = (String) myIter.next();
                // Si le path contient log comme
                // /cloudunit/software/tomcats/logs par exemple
                if (directoryInTheContainer != null
                    && directoryInTheContainer.contains("log")) {
                    Set<String> directoriesMounted = null;
                    directoriesMounted = volumesByContainer.get(key);
                    if (directoriesMounted == null) {
                        directoriesMounted = new HashSet<>();
                    }
                    // On récupère le chemin exposé en
                    // /var/lib/docker/vfs/dir/e23dke...
                    String directoryMountedInTheHost = volumes
                        .get(directoryInTheContainer);
                    if (logger.isDebugEnabled()) {
                        logger.debug("For containerID [" + key
                            + "] we add to the list : "
                            + directoryMountedInTheHost);
                    }
                    directoriesMounted.add(directoryMountedInTheHost);
                    volumesByContainer.put(key, directoriesMounted);
                }
            }
        }
    }

}
