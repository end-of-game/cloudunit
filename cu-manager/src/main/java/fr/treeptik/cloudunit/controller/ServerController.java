package fr.treeptik.cloudunit.controller;

import fr.treeptik.cloudunit.exception.CheckException;
import fr.treeptik.cloudunit.exception.ServiceException;
import fr.treeptik.cloudunit.json.ui.HttpOk;
import fr.treeptik.cloudunit.json.ui.JsonInput;
import fr.treeptik.cloudunit.json.ui.JsonResponse;
import fr.treeptik.cloudunit.model.Application;
import fr.treeptik.cloudunit.model.Server;
import fr.treeptik.cloudunit.model.Status;
import fr.treeptik.cloudunit.model.User;
import fr.treeptik.cloudunit.service.ApplicationService;
import fr.treeptik.cloudunit.service.ServerService;
import fr.treeptik.cloudunit.utils.AuthentificationUtils;
import fr.treeptik.cloudunit.utils.CheckUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.inject.Inject;
import java.io.Serializable;
import java.util.Locale;

@Controller
@RequestMapping("/server")
public class ServerController implements Serializable {

	private static final long serialVersionUID = 1L;

	private Logger logger = LoggerFactory.getLogger(ServerController.class);

	@Inject
	private ApplicationService applicationService;

	@Inject
	private ServerService serverService;

	@Inject
	private AuthentificationUtils authentificationUtils;

	private final Locale locale = Locale.ENGLISH;

	/**
	 * Set the JVM Options and Memory
	 * 
	 * @param input
	 * @return
	 * @throws ServiceException
	 * @throws CheckException
	 */
	@RequestMapping(value = "/configuration/jvm", method = RequestMethod.PUT)
	public @ResponseBody JsonResponse setOptionsJVM(@RequestBody JsonInput input)
			throws ServiceException, CheckException {

		if (logger.isDebugEnabled()) { logger.debug("" + input); }

		User user = authentificationUtils.getAuthentificatedUser();
		Application application = applicationService.findByNameAndUser(user,
				input.getApplicationName());

		authentificationUtils.canStartNewAction(user, application, locale);

		// Check the input for jvm options
		boolean isApplicatioRunning = application.getStatus().equals(Status.START);
		CheckUtils.checkJavaOpts(input.getJvmOptions(), input.getJvmMemory(), input.getJvmRelease());

		applicationService.setStatus(application, Status.PENDING);

		try {
			for (Server server : application.getServers()) {
				serverService.update(server, input.getJvmMemory(),
						input.getJvmOptions(), input.getJvmRelease(),
						input.getLocation());
			}
		} catch (Exception e) {
			// todo : do not flag fail but return an exception
			applicationService.setStatus(application, Status.FAIL);
		}

		// Si l'application était démarrée, on la redémarre
		if (isApplicatioRunning) {
			applicationService.setStatus(application, Status.PENDING);
			applicationService.stop(application);
			applicationService.start(application);
			applicationService.setStatus(application, Status.START);
		} else {
			applicationService.setStatus(application, Status.STOP);
		}

		return new HttpOk();
	}

	/**
	 *
	 * @param input
	 * @return
	 * @throws ServiceException
	 * @throws CheckException
	 */
	@RequestMapping(value = "/ports/open", method = RequestMethod.POST)
	public @ResponseBody JsonResponse openPort(@RequestBody JsonInput input)
			throws ServiceException, CheckException {

		User user = authentificationUtils.getAuthentificatedUser();
		Application application = applicationService.findByNameAndUser(user,
				input.getApplicationName());

		authentificationUtils.canStartNewAction(user, application, locale);

		boolean isApplicatioRunning = application.getStatus().equals(Status.START);

		applicationService.setStatus(application, Status.PENDING);

		serverService.openPort(input.getApplicationName(),
				input.getPortToOpen(), input.getAlias(), isApplicatioRunning);

		applicationService.setStatus(application, Status.START);

		return new HttpOk();
	}

	@RequestMapping(value = "/ports/close", method = RequestMethod.POST)
	public @ResponseBody JsonResponse closePort(@RequestBody JsonInput input)
			throws ServiceException, CheckException {

		User user = authentificationUtils.getAuthentificatedUser();
		Application application = applicationService.findByNameAndUser(user,
				input.getApplicationName());

		authentificationUtils.canStartNewAction(user, application, locale);

		return new HttpOk();
	}

}
