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

package fr.treeptik.cloudunit.controller;

import fr.treeptik.cloudunit.dto.LogLine;
import fr.treeptik.cloudunit.dto.SourceUnit;
import fr.treeptik.cloudunit.exception.CheckException;
import fr.treeptik.cloudunit.exception.ServiceException;
import fr.treeptik.cloudunit.service.FileService;
import fr.treeptik.cloudunit.utils.AuthentificationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.inject.Inject;
import java.util.List;

/**
 * Created by nicolas on 25/08/2014.
 */
@Controller
@RequestMapping("/logs")
public class LogController {

    private Logger logger = LoggerFactory.getLogger(LogController.class);

    @Inject
    private AuthentificationUtils authentificationUtils;

    @Autowired
    private FileService fileService;

    /**
     * Retourne les n-dernières lignes de logs d'une application
     *
     * @param applicationName
     * @param containerId
     * @param nbRows
     * @return
     * @throws ServiceException
     * @throws CheckException
     */
    @RequestMapping(value = "/{applicationName}/container/{containerId}/source/{source}/rows/{nbRows}", method = RequestMethod.GET)
    public
    @ResponseBody
    List<LogLine> findByApplication(
        @PathVariable String applicationName,
        @PathVariable String containerId, @PathVariable String source,
        @PathVariable Integer nbRows)
        throws ServiceException,
        CheckException {

        if (logger.isDebugEnabled()) {
            logger.debug("applicationName:" + applicationName);
            logger.debug("source:" + source);
            logger.debug("containerId:" + containerId);
            logger.debug("nbRows:" + nbRows);
        }

        if (nbRows < 1 || nbRows > 1000) {
            logger.info("Number of rows must be between 1 and 1000");
            nbRows = 500;
        }
        return fileService.catFileForNLines(containerId, source, nbRows);
    }

    /**
     * retourne la liste des fichers possibles (les sources) nécessaires pour la
     * panneau de logs
     */
    @RequestMapping(value = "/sources/{applicationName}/container/{containerId}", method = RequestMethod.GET)
    public
    @ResponseBody
    List<SourceUnit> findByApplication(
        @PathVariable String applicationName,
        @PathVariable String containerId)
        throws ServiceException,
        CheckException {

        if (logger.isDebugEnabled()) {
            logger.debug("applicationName:" + applicationName);
            logger.debug("containerId:" + containerId);
        }

        List<SourceUnit> sources = fileService.listLogsFilesByContainer(containerId);
        // needed by UI to call the next url
        if (sources.size()==0) {
            String defaultFile = fileService.getDefaultLogFile(containerId);
            sources.add(new SourceUnit(defaultFile));
        }
        logger.debug("" + sources);

        return sources;
    }

}
