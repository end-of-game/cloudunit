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

import java.io.Serializable;
import java.util.Locale;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import fr.treeptik.cloudunit.aspects.CloudUnitSecurable;
import fr.treeptik.cloudunit.config.events.ApplicationFailEvent;
import fr.treeptik.cloudunit.config.events.ApplicationPendingEvent;
import fr.treeptik.cloudunit.config.events.ApplicationStartEvent;
import fr.treeptik.cloudunit.dto.HttpOk;
import fr.treeptik.cloudunit.dto.JsonInput;
import fr.treeptik.cloudunit.dto.JsonResponse;
import fr.treeptik.cloudunit.exception.CheckException;
import fr.treeptik.cloudunit.exception.ServiceException;
import fr.treeptik.cloudunit.model.Application;
import fr.treeptik.cloudunit.model.Status;
import fr.treeptik.cloudunit.model.User;
import fr.treeptik.cloudunit.service.ApplicationService;
import fr.treeptik.cloudunit.service.ModuleService;
import fr.treeptik.cloudunit.utils.AuthentificationUtils;

@Controller
@RequestMapping("/module")
public class ModuleController implements Serializable {

	private static final long serialVersionUID = 1L;

	Locale locale = Locale.ENGLISH;

	private Logger logger = LoggerFactory.getLogger(ModuleController.class);

	@Inject
	private ModuleService moduleService;

	@Inject
	private ApplicationService applicationService;

	@Inject
	private AuthentificationUtils authentificationUtils;

	@Inject
	private ApplicationEventPublisher applicationEventPublisher;

	/**
	 * Add a module to an existing application
	 *
	 * @param input
	 * @return
	 * @throws ServiceException
	 * @throws CheckException
	 */
	@CloudUnitSecurable
	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	public JsonResponse addModule(@RequestBody JsonInput input) throws ServiceException, CheckException {
		// validate the input
		input.validateAddModule();

		String applicationName = input.getApplicationName();

		String imageName = input.getImageName();

		User user = authentificationUtils.getAuthentificatedUser();
		Application application = applicationService.findByNameAndUser(user, applicationName);
		applicationEventPublisher.publishEvent(new ApplicationPendingEvent(application));
		try {
			moduleService.create(imageName, application, user);
			logger.info("--initModule " + imageName + " to " + applicationName + " successful--");
			applicationEventPublisher.publishEvent(new ApplicationStartEvent(application));

		} catch (Exception e) {
			logger.error(input.toString(), e);
			applicationEventPublisher.publishEvent(new ApplicationFailEvent(application));

		}

		return new HttpOk();
	}

	/**
	 * Add a module to an existing application
	 *
	 * @param input
	 * @return
	 * @throws ServiceException
	 * @throws CheckException
	 */
	@CloudUnitSecurable
	@RequestMapping(method = RequestMethod.PUT, value = "/{id}")
	@ResponseBody
	public JsonResponse publishPort(@PathVariable("id") Integer id, @RequestBody JsonInput input)
			throws ServiceException, CheckException {
		input.validatePublishPort();

		String applicationName = input.getApplicationName();

		User user = authentificationUtils.getAuthentificatedUser();
		Application application = applicationService.findByNameAndUser(user, applicationName);
		applicationEventPublisher.publishEvent(new ApplicationPendingEvent(application));
		moduleService.publishPort(id, input.getPublishPort(), applicationName, user);
		applicationEventPublisher.publishEvent(new ApplicationStartEvent(application));
		return new HttpOk();
	}

	/**
	 * Remove a module to an existing application
	 *
	 * @param jsonInput
	 * @return
	 * @throws ServiceException
	 * @throws CheckException
	 */
	@CloudUnitSecurable
	@RequestMapping(value = "/{applicationName}/{moduleName}", method = RequestMethod.DELETE)
	@ResponseBody
	public JsonResponse removeModule(JsonInput jsonInput) throws ServiceException, CheckException {

		// validate the input
		jsonInput.validateRemoveModule();

		String applicationName = jsonInput.getApplicationName();
		String moduleName = jsonInput.getModuleName();

		User user = authentificationUtils.getAuthentificatedUser();
		Application application = applicationService.findByNameAndUser(user, applicationName);

		// We must be sure there is no running action before starting new one
		authentificationUtils.canStartNewAction(user, application, locale);

		Status previousApplicationStatus = application.getStatus();
		try {
			// Application occupée
			applicationService.setStatus(application, Status.PENDING);

			moduleService.remove(user, moduleName, true, previousApplicationStatus);

			logger.info("-- removeModule " + applicationName + " to " + moduleName + " successful-- ");

		} catch (Exception e) {
			// Application en erreur
			logger.error(applicationName + " // " + moduleName, e);
		} finally {
			applicationService.setStatus(application, previousApplicationStatus);
		}

		return new HttpOk();
	}

}
