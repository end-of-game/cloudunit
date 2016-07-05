package fr.treeptik.cloudunit.utils;

import fr.treeptik.cloudunit.model.Application;

import java.io.UnsupportedEncodingException;

/**
 * Created by guillaume on 05/07/16.
 */
public interface KeyValueStoreUtils {
void createRedisAppKey(Application application,
                                  String dockerManagerIP, String serverPort, String serverManagerPort);void writeNewAlias(String alias, Application application, String serverPort);void updateAlias(String alias, Application application, String serverPort);void updateServerAddress(Application application,
                                    String dockerManagerIP, String serverPort, String serverManagerPort);void removeServerAddress(Application application);

    void updatePortAlias(
            String serverIP, Integer port, String portAlias);

    void removeServerPortAlias(String portAlias);


    void removeRedisAppKey(Application application);

    void createModuleManagerKey(Application application,
                                       String dockerContainerIP, String modulePort,
                                       String cloudunitModuleManagerSuffix, Long instanceNumber);

    void updatedAdminAddress(Application application,
                                    String dockerManagerIP, String modulePort,
                                    String cloudunitModuleManagerSuffix, Long instanceNumber);

    void removePhpMyAdminKey(Application application,
                                    String cloudunitModuleManagerSuffix, Long instanceNumber);

     void removeAlias(String alias);

}
