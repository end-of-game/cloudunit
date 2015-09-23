package fr.treeptik.cloudunit.service;

import fr.treeptik.cloudunit.exception.CheckException;
import fr.treeptik.cloudunit.exception.ServiceException;
import fr.treeptik.cloudunit.json.ui.ContainerUnit;
import fr.treeptik.cloudunit.model.Application;
import fr.treeptik.cloudunit.model.Status;
import fr.treeptik.cloudunit.model.User;

import java.io.File;
import java.util.List;

public interface ApplicationService {

    Application findByNameAndUser(User user, String applicationName)
            throws ServiceException, CheckException;

    List<Application> findAll() throws ServiceException;

    List<Application> findAllByUser(User user) throws ServiceException;

    Long countApp(User user) throws ServiceException;

    void isValid(String applicationName, String serverName)
            throws ServiceException, CheckException;

    void checkCreate(Application application, String serverName)
            throws CheckException, ServiceException;

    Application saveInDB(Application application) throws ServiceException;

    void checkStatus(Application application, String status)
            throws CheckException, ServiceException;

    boolean checkAppExist(User user, String applicationName)
            throws ServiceException, CheckException;

    void setStatus(Application application, Status status)
            throws ServiceException;

    Application deploy(File file, Application application)
            throws ServiceException, CheckException;

    Application start(Application application) throws ServiceException;

    Application stop(Application application) throws ServiceException;

    List<String> listGitTagsOfApplication(String applicationName)
            throws ServiceException;

    Application saveGitPush(String applicationName, String login)
            throws ServiceException, CheckException;

    String initApplicationWithGitHub(String applicationName, String gitAddress)
            throws ServiceException;

    List<ContainerUnit> listContainers(String applicationName)
            throws ServiceException;

    List<String> listContainersId(String applicationName)
            throws ServiceException;

    List<String> getListAliases(Application application)
            throws ServiceException;

    void addNewAlias(Application application, String alias)
            throws ServiceException, CheckException;

    void updateAliases(Application application) throws ServiceException;

    void removeAlias(Application application, String alias) throws ServiceException;

    Application updateEnv(Application application, User user)
            throws ServiceException;

    Application postStart(Application application, User user)
            throws ServiceException;

    Application remove(Application application, User user)
            throws ServiceException;

    Application sshCopyIDToServer(Application application, User user)
            throws ServiceException;

    Application create(String applicationName, String login, String serverName, String tagName)
            throws ServiceException, CheckException;

}
