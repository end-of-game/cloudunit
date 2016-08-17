/*
 * LICENCE : CloudUnit is available under the GNU Affero General Public License : https://gnu.org/licenses/agpl.html
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
package fr.treeptik.cloudunit.cli.utils;

import fr.treeptik.cloudunit.cli.commands.ShellStatusCommand;
import fr.treeptik.cloudunit.cli.exception.ManagerResponseException;
import fr.treeptik.cloudunit.cli.model.Application;
import fr.treeptik.cloudunit.cli.model.Image;
import fr.treeptik.cloudunit.cli.processor.InjectLogger;
import fr.treeptik.cloudunit.cli.rest.JsonConverter;
import fr.treeptik.cloudunit.cli.rest.RestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class CheckUtils {

    @InjectLogger
    private Logger log;

    @Autowired
    private ShellStatusCommand statusCommand;

    @Autowired
    private RestUtils restUtils;

    @Autowired
    private ApplicationUtils applicationUtils;

    @Autowired
    private AuthentificationUtils authentificationUtils;

    @Autowired
    private UrlLoader urlLoader;

    /**
     * TODO refactore
     */
    public boolean checkImageNoExist(String imageName) throws ManagerResponseException {

        List<Image> images = null;
        try {
            images = JsonConverter.getImages(restUtils.sendGetCommand(
                    authentificationUtils.finalHost + urlLoader.imageFind + "/all",
                    authentificationUtils.getMap()).get("body"));
        } catch (ManagerResponseException e) {
            throw new ManagerResponseException(e.getMessage(), e);
        }
        List<String> imageNames = new ArrayList<>();
        for (Image image : images) {
            imageNames.add(image.getName());
        }

        if (!imageNames.contains(imageName)) {
            log.log(Level.SEVERE,
                    "the service you want to install doesn't exist/activated or spell check");
            statusCommand.setExitStatut(1);
            return true;
        } else
            return false;
    }

    public boolean checkImageNoEnabled(String imageName) throws ManagerResponseException {

        List<Image> images = null;
        try {
            images = JsonConverter.getImages(restUtils.sendGetCommand(
                    authentificationUtils.finalHost + urlLoader.adminActions
                            + urlLoader.imageEnabled,
                    authentificationUtils.getMap()).get("body"));
        } catch (ManagerResponseException e) {
            throw new ManagerResponseException(e.getMessage(), e);

        }
        List<String> imageNames = new ArrayList<>();
        for (Image image : images) {
            imageNames.add(image.getName());
        }

        if (!imageNames.contains(imageName)) {
            log.log(Level.SEVERE,
                    "The service you want to use is not activated for this version");
            statusCommand.setExitStatut(1);
            return true;
        } else
            return false;
    }

    public boolean checkApplicationExist(String applicationName) throws ManagerResponseException {
        List<String> listApplicationNames = new ArrayList<>();
        List<Application> listApplication = null;
        try {
            listApplication = applicationUtils.listAllApps();
        } catch (ManagerResponseException e) {
            throw new ManagerResponseException(e.getMessage(), e);
        }
        if (listApplication.size() == 0) {
            return false;
        } else {
            for (Application application : listApplication) {
                listApplicationNames.add(application.getName());
            }

            if (listApplicationNames.contains(applicationName)) {
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * TODO chiffre codé en dur à refactorer
     *
     * @param application
     * @return
     */
    public boolean checkNumberofServers(Application application) {
        if (application.getServers().size() >= 1) {
            log.log(Level.SEVERE,
                    "This application have already the max number of instance of this service");
            statusCommand.setExitStatut(1);
            return false;
        } else {
            return true;
        }
    }

}
