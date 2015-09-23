package fr.treeptik.cloudunit.controller;

import fr.treeptik.cloudunit.exception.CheckException;
import fr.treeptik.cloudunit.exception.ServiceException;
import fr.treeptik.cloudunit.json.ui.LogUnit;
import fr.treeptik.cloudunit.json.ui.SourceUnit;
import fr.treeptik.cloudunit.service.FileService;
import fr.treeptik.cloudunit.service.LogService;
import fr.treeptik.cloudunit.utils.AuthentificationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

	@Inject
	private LogService logService;

	@Inject
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
	public @ResponseBody List<LogUnit> findByApplication(
			@PathVariable String applicationName,
			@PathVariable String containerId, @PathVariable String source,
			@PathVariable Integer nbRows) throws ServiceException,
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

		return logService.listByApp(applicationName, containerId, source, nbRows);
	}

	/**
	 * retourne la liste des fichers possibles (les sources) nécessaires pour la
	 * panneau de logs
	 *
	 */
	@RequestMapping(value = "/sources/{applicationName}/container/{containerId}", method = RequestMethod.GET)
	public @ResponseBody List<SourceUnit> findByApplication(
			@PathVariable String applicationName,
			@PathVariable String containerId) throws ServiceException,
			CheckException {

		if (logger.isDebugEnabled()) {
			logger.debug("applicationName:" + applicationName);
			logger.debug("containerId:" + containerId);
		}

		List<SourceUnit> sources = fileService.listLogsFilesByContainer(containerId);
		logger.debug("" + sources);

		return sources;
	}

}
