package fr.treeptik.cloudunit.controller;

import fr.treeptik.cloudunit.exception.CheckException;
import fr.treeptik.cloudunit.exception.ServiceException;
import fr.treeptik.cloudunit.model.Message;
import fr.treeptik.cloudunit.model.User;
import fr.treeptik.cloudunit.service.MessageService;
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
 * Created by nicolas on 28/08/2014.
 */
@Controller
@RequestMapping("/messages")
public class MessageController {

	private Logger logger = LoggerFactory.getLogger(MessageController.class);

	@Inject
	private MessageService messageService;

	@Inject
	private AuthentificationUtils authentificationUtils;

	/**
	 * Retourne tous les messages d'un utilisateur pour une application
	 * 
	 * @param applicationName
	 * @return
	 * @throws ServiceException
	 * @throws CheckException
	 */
	@RequestMapping(value = "/application/{applicationName}", method = RequestMethod.GET)
	public @ResponseBody List<Message> listMessagesForApplication(
			@PathVariable final String applicationName)
			throws ServiceException, CheckException {
		if (logger.isDebugEnabled()) {
			logger.info("--CALL LIST APPLICATION ACTIONS--");
			logger.debug("applicationName = " + applicationName);
		}
		// Retourne par défaut le 10 derniers messages
		User user = authentificationUtils.getAuthentificatedUser();
		return messageService.listByApp(user, applicationName, 10);
	}

	/**
	 * Retourne tous les messages d'un utilisateur pour une application
	 * 
	 * @param applicationName
	 * @return
	 * @throws ServiceException
	 * @throws CheckException
	 */
	@RequestMapping(value = "/application/{applicationName}/rows/{nbRows}", method = RequestMethod.GET)
	public @ResponseBody List<Message> listMessagesForApplication(
			@PathVariable final String applicationName,
			@PathVariable final Integer nbRows) throws ServiceException,
			CheckException {
		if (logger.isDebugEnabled()) {
			logger.debug("--CALL LIST APPLICATION ACTIONS--");
			logger.debug("applicationName = " + applicationName);
			logger.debug("nbRows = " + nbRows);
		}
		User user = authentificationUtils.getAuthentificatedUser();
		return messageService.listByApp(user, applicationName, nbRows);
	}

	/**
	 * Retourne tous les messages pour un utilisateur quelque soit son
	 * application
	 * 
	 * @return
	 * @throws ServiceException
	 * @throws CheckException
	 */
	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody List<Message> listMessages() throws ServiceException,
			CheckException {
		return messageService.listByUser(
				authentificationUtils.getAuthentificatedUser(), 0);
	}

	/**
	 * Retourne tous les messages pour un utilisateur quelque soit son
	 * application
	 * 
	 * @return
	 * @throws ServiceException
	 * @throws CheckException
	 */
	@RequestMapping(value = "/rows/{nbRows}", method = RequestMethod.GET)
	public @ResponseBody List<Message> listMessages(
			@PathVariable final Integer nbRows) throws ServiceException,
			CheckException {
		if (logger.isDebugEnabled()){
			logger.debug("nbRows:" + nbRows);
		}
		return messageService.listByUser(
				authentificationUtils.getAuthentificatedUser(), nbRows);
	}

}
