package fr.treeptik.cloudunit.controller;

import fr.treeptik.cloudunit.exception.CheckException;
import fr.treeptik.cloudunit.exception.ServiceException;
import fr.treeptik.cloudunit.service.MonitoringService;
import fr.treeptik.cloudunit.utils.AuthentificationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by nicolas on 25/08/2014.
 */
@Controller
@RequestMapping("/monitoring")
public class MonitoringController {

	private Logger logger = LoggerFactory.getLogger(MonitoringController.class);

	@Inject
	private AuthentificationUtils authentificationUtils;

	@Inject
	private MonitoringService monitoringService;

	/**
	 * Is a wrapper to cAdvisor API
	 *
	 * @return
	 * @throws fr.treeptik.cloudunit.exception.ServiceException
	 * @throws fr.treeptik.cloudunit.exception.CheckException
	 */
	@RequestMapping(value = "/api/machine", method = RequestMethod.GET)
	public String infoMachine() throws ServiceException,
			CheckException {
		return monitoringService.getJsonMachineFromCAdvisor();
	}

    /**
     * * Is a wrapper to cAdvisor API
     * @param containerName
     * @throws ServiceException
     * @throws CheckException
     */
	@RequestMapping(value = "/api/containers/docker/{containerName}", method = RequestMethod.GET)
	public void infoContainer(HttpServletRequest request, HttpServletResponse response,
                              @PathVariable String containerName)
			throws ServiceException, CheckException {
		String containerId = monitoringService
				.getFullContainerId(containerName);

		String responseFromCAdvisor = monitoringService.getJsonFromCAdvisor(containerId);

		if (logger.isDebugEnabled()) {
			logger.debug("containerId=" + containerId);
			logger.debug("responseFromCAdvisor=" + responseFromCAdvisor);
		}

        try {
            response.getWriter().write(responseFromCAdvisor);
            response.flushBuffer();
        } catch(Exception e) {
            logger.error("error during write and flush response", containerName);
        }
	}

}
