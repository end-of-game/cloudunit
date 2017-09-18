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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.codahale.metrics.Counter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import fr.treeptik.cloudunit.dto.LogResource;
import fr.treeptik.cloudunit.dto.SourceUnit;
import fr.treeptik.cloudunit.exception.CheckException;
import fr.treeptik.cloudunit.exception.ServiceException;
import fr.treeptik.cloudunit.factory.LogResourceFactory;
import fr.treeptik.cloudunit.logs.GatheringStrategy;
import fr.treeptik.cloudunit.service.FileService;

/**
 * Created by nicolas on 25/08/2014.
 */
@Controller
@RequestMapping("/logs")
public class LogController {

	private Logger logger = LoggerFactory.getLogger(LogController.class);

    @Autowired
    Map<String, GatheringStrategy> gatheringStrategies = new HashMap<>();

    @Autowired
    private FileService fileService;

    @Autowired
	private Counter logsDisplayCalls;

	/**
	 * Returns the n-last lines for an application / container
	 *
	 * @param applicationName
	 * @param container
	 * @param nbRows
	 * @return
	 * @throws ServiceException
	 * @throws CheckException
	 */
	@RequestMapping(value = "/{applicationName}/container/{container}/source/{source}/rows/{nbRows}", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<List<LogResource>> findByApplication(
            @PathVariable String applicationName, @PathVariable String container,
            @PathVariable String source, @PathVariable Integer nbRows)
			throws ServiceException, CheckException {

		if (logger.isDebugEnabled()) {
			logger.debug("applicationName:" + applicationName);
			logger.debug("source:" + source);
			logger.debug("containerId:" + container);
			logger.debug("nbRows:" + nbRows);
		}

		logsDisplayCalls.inc();

		// We could expect stdout as strategy
        GatheringStrategy gatheringStrategy =
                gatheringStrategies.getOrDefault(source, gatheringStrategies.get("tail"));

        String logs = gatheringStrategy.gather(container, source, nbRows);
        List<LogResource> logResources = LogResourceFactory.fromOutput(logs);
        return ResponseEntity.status(HttpStatus.OK).body(logResources);
	}

	/**
	 * Return the list of possible list files
	 */
	@RequestMapping(value = "/sources/{applicationName}/container/{containerId}", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<List<SourceUnit>> findByApplication(
            @PathVariable String applicationName, @PathVariable String containerId)
            throws ServiceException, CheckException {
		if (logger.isDebugEnabled()) {
			logger.debug("applicationName:" + applicationName);
			logger.debug("containerId:" + containerId);
		}
		List<SourceUnit> sources = fileService.listLogsFilesByContainer(containerId);
		if (sources.size() == 0) {
			String defaultFile = fileService.getLogDirectory(containerId);
			sources.add(new SourceUnit(defaultFile));
		}
        return ResponseEntity.status(HttpStatus.OK).body(sources);
	}

}
