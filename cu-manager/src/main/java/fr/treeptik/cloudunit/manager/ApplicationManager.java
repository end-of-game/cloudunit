package fr.treeptik.cloudunit.manager;

import fr.treeptik.cloudunit.exception.CheckException;
import fr.treeptik.cloudunit.exception.ServiceException;
import fr.treeptik.cloudunit.model.Application;
import fr.treeptik.cloudunit.model.Status;
import fr.treeptik.cloudunit.model.User;
import fr.treeptik.cloudunit.utils.FilesUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Locale;

/**
 * Created by nicolas on 21/09/15.
 */
public interface ApplicationManager {

    public void create(final String applicationName, final String userLogin, final String serverName)
            throws ServiceException, CheckException;

    public void start(Application application, User user)
            throws ServiceException, CheckException;

    public void stop(Application application, User user)
            throws ServiceException, CheckException;

    public void deploy(MultipartFile fileUpload, Application application)
            throws ServiceException, CheckException;
}
