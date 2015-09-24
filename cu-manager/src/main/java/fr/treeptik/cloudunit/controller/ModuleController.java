/*
 * LICENCE : CloudUnit is available under the Gnu Public License GPL V3 : https://www.gnu.org/licenses/gpl.txt
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

package fr.treeptik.cloudunit.controller;

import fr.treeptik.cloudunit.exception.CheckException;
import fr.treeptik.cloudunit.exception.ServiceException;
import fr.treeptik.cloudunit.json.ui.HttpOk;
import fr.treeptik.cloudunit.json.ui.JsonInput;
import fr.treeptik.cloudunit.json.ui.JsonResponse;
import fr.treeptik.cloudunit.model.*;
import fr.treeptik.cloudunit.service.ApplicationService;
import fr.treeptik.cloudunit.service.ModuleService;
import fr.treeptik.cloudunit.utils.AuthentificationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Locale;

@Controller
@RequestMapping("/module")
public class ModuleController implements Serializable {

	private static final long serialVersionUID = 1L;

	private Logger logger = LoggerFactory.getLogger(ModuleController.class);

	@Inject
	private ModuleService moduleService;

	@Inject
	private ApplicationService applicationService;

	@Inject
	private AuthentificationUtils authentificationUtils;

	@Inject
	private MessageSource messageSource;

	Locale locale = LocaleContextHolder.getLocale();

	/**
	 * Add a module to an existing application
	 * 
	 * @param input
	 * @return
	 * @throws ServiceException
	 * @throws CheckException
	 */
	@RequestMapping(method = RequestMethod.POST)
	public @ResponseBody
	JsonResponse addModule(@RequestBody JsonInput input)
			throws ServiceException, CheckException {

		String applicationName = input.getApplicationName();
		logger.info("input.getApplicationName():" + applicationName);
		logger.info("input.getImageName():" + input.getImageName());

		User user = authentificationUtils.getAuthentificatedUser();
		Application application = applicationService.findByNameAndUser(user,
				input.getApplicationName());

		// We must be sure there is no running action before starting new one
		this.authentificationUtils.canStartNewAction(user, application, locale);

		// check if there is no action currently on the entity
		Status previousStatus = application.getStatus();

		try {
			// Application occupée
			applicationService.setStatus(application, Status.PENDING);

			Module module = ModuleFactory.getModule(input.getImageName());

			moduleService.checkImageExist(input.getImageName());

			module.getImage().setName(input.getImageName());
			module.setName(input.getImageName());
			module.setApplication(application);

			moduleService.initModule(application, module, null);

			logger.info("--initModule " + input.getImageName() + " to "
					+ input.getApplicationName() + " successful--");

		} catch (Exception e) {
			logger.error(input.toString(), e);
		} finally {
			applicationService.setStatus(application, previousStatus);
		}

		return new HttpOk();
	}

	/**
	 * Remove a module to an existing application
	 * 
	 * @param applicationName
	 * @param moduleName
	 * @return
	 * @throws ServiceException
	 * @throws CheckException
	 */
	@RequestMapping(value = "/{applicationName}/{moduleName}", method = RequestMethod.DELETE)
	public @ResponseBody
	JsonResponse removeModule(@PathVariable String applicationName,
			@PathVariable String moduleName) throws ServiceException,
			CheckException {

		if (logger.isInfoEnabled()) {
			logger.info("applicationName:" + applicationName);
			logger.info("moduleName:" + moduleName);
		}

		User user = authentificationUtils.getAuthentificatedUser();
		Application application = applicationService.findByNameAndUser(user,
				applicationName);

		// We must be sure there is no running action before starting new one
		authentificationUtils.canStartNewAction(user, application, locale);

		Status previousApplicationStatus = application.getStatus();
		try {
			// Application occupée
			applicationService.setStatus(application, Status.PENDING);

			Module module = moduleService.findByName(moduleName);
			moduleService.remove(application, user, module, true,
					previousApplicationStatus);

			logger.info("-- removeModule " + applicationName + " to "
					+ moduleName + " successful-- ");

		} catch (Exception e) {
			// Application en erreur
			logger.error(applicationName + " // " + moduleName, e);
		} finally {
			applicationService.setStatus(application,	previousApplicationStatus);
		}

		return new HttpOk();
	}

    @ResponseBody
	@RequestMapping(value = "/{applicationName}/{moduleName}/initData",
            method = RequestMethod.POST,
            consumes = { "multipart/form-data" })
	public JsonResponse deploy(@RequestPart("file") MultipartFile fileUpload,
			@PathVariable final String applicationName,
			@PathVariable final String moduleName, HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServiceException,
			CheckException {

		logger.info("initDb : applicationName = " + applicationName
				+ ", moduleName = " + moduleName);

		User user = authentificationUtils.getAuthentificatedUser();
		Application application = applicationService.findByNameAndUser(user,
				applicationName);

		// We must be sure there is no running action before starting new one
		this.authentificationUtils.canStartNewAction(user, application, locale);

		File file = null;
		try {

			// Application occupée
			applicationService.setStatus(application, Status.PENDING);

			file = File.createTempFile("script-",
					fileUpload.getOriginalFilename());
			fileUpload.transferTo(file);

			moduleService.initDb(user, applicationName, moduleName, file);

		} catch (IOException e) {
			throw new ServiceException("initDb Error while creating file", e);
		} finally {
			applicationService.setStatus(application, Status.START);
		}
		return new HttpOk();
	}
}
