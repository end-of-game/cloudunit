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

package fr.treeptik.cloudunit.aspects;

import java.io.Serializable;

import javax.inject.Inject;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.JoinPoint.StaticPart;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.treeptik.cloudunit.exception.MonitorException;
import fr.treeptik.cloudunit.exception.ServiceException;
import fr.treeptik.cloudunit.model.Message;
import fr.treeptik.cloudunit.model.Server;
import fr.treeptik.cloudunit.model.User;
import fr.treeptik.cloudunit.service.MessageService;
import fr.treeptik.cloudunit.utils.MessageUtils;

public class ServerAspect extends CloudUnitAbstractAspect implements Serializable {

	private static final long serialVersionUID = 1L;

	private final static String createType = "CREATE";

	private final static String updateType = "UPDATE";

	private final Logger logger = LoggerFactory.getLogger(ServerAspect.class);

	@Inject
	private MessageService messageService;

	// Before methods
	@Before("execution(* fr.treeptik.cloudunit.service.ServerService.updateType(..))")
	public void beforeServer(JoinPoint joinPoint) throws MonitorException, ServiceException {

		Server server = (Server) joinPoint.getArgs()[0];
		User user = getAuthentificatedUser();
		Message message = null;
		// String applicationName = server.getApplication().getName();
		String applicationDisplayName = server.getApplication().getDisplayName();

		switch (joinPoint.getSignature().getName().toUpperCase()) {
		case updateType:
			message = MessageUtils.writeBeforeApplicationMessage(user, applicationDisplayName, updateType);
			break;
		}
		logger.info(message.toString());
		messageService.create(message);

	}

	@AfterReturning(pointcut = "execution(* fr.treeptik.cloudunit.service.ServerService.create(..)) "
			+ "|| execution(* fr.treeptik.cloudunit.service.ServerService.updateType(..))", returning = "result")
	public void afterReturningServer(StaticPart staticPart, Object result) throws MonitorException {
		try {
			if (result == null)
				return;
			Server server = (Server) result;
			User user = server.getApplication().getUser();
			Message message = null;
			switch (staticPart.getSignature().getName().toUpperCase()) {
			case createType:
				message = MessageUtils.writeServerMessage(user, server, createType);
				break;
			case updateType:
				message = MessageUtils.writeServerMessage(user, server, updateType);
				break;

			}
			logger.info(message.toString());
			messageService.create(message);

		} catch (ServiceException e) {
			throw new MonitorException("Error afterReturningApplication", e);
		}
	}

	@AfterThrowing(pointcut = "execution(* fr.treeptik.cloudunit.service.ServerService.updateType(..))", throwing = "e")
	public void afterThrowingServer(StaticPart staticPart, Exception e) throws ServiceException {
		User user = this.getAuthentificatedUser();
		Message message = null;
		logger.debug("CALLED CLASS : " + staticPart.getSignature().getName());
		switch (staticPart.getSignature().getName().toUpperCase()) {
		case updateType:
			message = MessageUtils.writeAfterThrowingModuleMessage(e, user, updateType);
			break;
		}
		if (message != null) {
			messageService.create(message);
		}
	}

}
