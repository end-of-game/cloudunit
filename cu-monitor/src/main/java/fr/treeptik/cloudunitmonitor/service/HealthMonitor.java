package fr.treeptik.cloudunitmonitor.service;

import java.io.IOException;
import java.security.ProviderException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Inject;
import javax.mail.MessagingException;

import fr.treeptik.cloudunitmonitor.conf.ApplicationEntryPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import fr.treeptik.cloudunitmonitor.docker.model.DockerContainer;
import fr.treeptik.cloudunitmonitor.docker.model.DockerContainerBuilder;
import fr.treeptik.cloudunitmonitor.exception.DockerJSONException;
import fr.treeptik.cloudunitmonitor.exception.ServiceException;
import fr.treeptik.cloudunitmonitor.model.ErrorMessage;
import fr.treeptik.cloudunitmonitor.model.Module;
import fr.treeptik.cloudunitmonitor.model.Server;
import fr.treeptik.cloudunitmonitor.model.Status;
import fr.treeptik.cloudunitmonitor.utils.EmailUtils;

@Service
public class HealthMonitor {

	private Logger logger = LoggerFactory.getLogger(HealthMonitor.class);

	private Map<String, Integer> elementsInError = new ConcurrentHashMap<>();

	private Long count = 0L;

	@Inject
	private EmailUtils emailUtils;

	@Inject
	private ModuleService moduleService;

	@Inject
	private ServerService serverService;

	@Inject
	private ErrorMessageService errorMessageService;

	public int checkAllServersAndModules() throws ServiceException,
			MessagingException, IOException {

		logger.info("Errors found : " + elementsInError.size());
		logger.info("Errors resolved : " + count);

		/*
		 * On charge les listes des serveurs et modules présent en base
		 */

		List<Server> servers = serverService.findAll();
		List<Module> modules = moduleService.findAll();

		logger.info(servers.size() + " servers found");
		logger.info(modules.size() + " modules found");
		logger.info(elementsInError.toString());

		try {

			// On vérifie d'abord la validité des modules

			for (Module module : modules) {

				DockerContainer container;
				container = DockerContainer.findOne(buildContainerName(module),
						module.getApplication().getManagerIp());

				// Si le container du module est sensé être démarré et que son
				// statut est "paused" on le met en erreur

				if (module.getStatus().equals(Status.START)) {

					if (!container.getState().equalsIgnoreCase("Running")) {

						elementsInError = buildMapError(elementsInError,
								container);

						// l'erreur est résolu après un décompte de 3 tours de
						// boucle

						if (elementsInError.get(container.getName()) > 3) {
							moduleService.startModule(module);

							errorMessageService
									.create(buildNewErrorMessage(" Un problème est survenu : Le container n° "
											+ container.getName()
											+ " "
											+ container.getId()
											+ " a dû être redémarré hors du contexte du manager"));

						}

					} else {

						// sinon, si le container en erreur est toujours présent
						// dans la liste d'erreur mais qu'il ne l'est pas, on
						// l'éjecte et on le met incrémente la liste des erreurs
						// résolus

						if (elementsInError.containsKey(container.getName())) {
							elementsInError.remove(container.getName());
							count++;
						}

					}

				} else if (module.getStatus().equals(Status.STOP)) {
					if (container.getState().equalsIgnoreCase("Running")) {

						elementsInError = buildMapError(elementsInError,
								container);

						if (elementsInError.get(container.getName()) > 3) {

							if (module.getImage().getImageType()
									.equals("module")) {
								DockerContainer dataContainer = new DockerContainer();
								dataContainer.setName(module.getName()
										+ "-data");
								DockerContainer.stop(dataContainer, module
										.getApplication().getManagerIp());
							}

							DockerContainer.stop(container, module
									.getApplication().getManagerIp());

							logger.info("j'ai repéré une erreur");

							errorMessageService
									.create(buildNewErrorMessage(" Un problème est survenu : Le container n° "
											+ container.getName()
											+ " "
											+ container.getId()
											+ " a dû être éteint hors du contexte du manager"));
						}

					} else {

						if (elementsInError.containsKey(container.getName())) {
							elementsInError.remove(container.getName());
							count++;
						}

					}

				}
			}

			// Même chose pour les servers

			for (Server server : servers) {

				DockerContainer container = DockerContainer.findOne(
						buildContainerName(server), server.getApplication()
								.getManagerIp());

				if (server.getStatus().equals(Status.START)) {

					if (!container.getState().equalsIgnoreCase("Running")) {

						elementsInError = buildMapError(elementsInError,
								container);

						if (elementsInError.get(container.getName()) > 3) {
							serverService.startServer(server);

							logger.info("j'ai repéré une erreur");
							errorMessageService
									.create(buildNewErrorMessage(" Un problème est survenu : Le container n° "
											+ container.getName()
											+ " "
											+ container.getId()
											+ " a dû être redémarré hors du contexte du manager"));
						}

					} else {

						if (elementsInError.containsKey(container.getName())) {
							elementsInError.remove(container.getName());
							count++;
						}
					}

				} else if (server.getStatus().equals(Status.STOP)) {
					if (container.getState().equalsIgnoreCase("Running")) {

						elementsInError = buildMapError(elementsInError,
								container);

						if (elementsInError.get(container.getName()) > 3) {
							DockerContainer.stop(container, server
									.getApplication().getManagerIp());
							errorMessageService
									.create(buildNewErrorMessage(" Un problème est survenu : Le container n° "
											+ container.getName()
											+ " "
											+ container.getId()
											+ " a dû être éteint hors du contexte du manager"));
						}

					} else {

						if (elementsInError.containsKey(container.getName())) {
							elementsInError.remove(container.getName());
							count++;
						}
					}

				}
			}

		} catch (ProviderException | DockerJSONException e) {
			logger.info("Error : find container failed" + e.getMessage());
			Boolean alreadyCheckUnknownError = false;
			List<ErrorMessage> findAllUnchecked = errorMessageService
					.findAllUnchecked();

			for (ErrorMessage errorMessage : findAllUnchecked) {
				if (errorMessage.getMessage().contains("synchronisé")) {
					alreadyCheckUnknownError = true;
					break;
				}
			}
			if (!alreadyCheckUnknownError) {
				errorMessageService
						.create(buildNewErrorMessage("Une erreur est survenue lors du redémarrage ou de l'extinction d'un container non synchronisé : "
								+ e.getMessage()));
			}
			e.printStackTrace();
			return 2;
		}

		if (!elementsInError.isEmpty()) {
			return 1;
		}

		return 0;
	}

	@Scheduled(cron = "0 0 16 * * ?")
	public void sendEmailToAdmin() throws ServiceException {
		try {

			logger.info("*** Sending email to Admin***");
			List<ErrorMessage> errorMessages = errorMessageService
					.findAllUnchecked();
			String globalMessage = "";

			// get all unchecked error messages and build the body
			for (ErrorMessage errorMessage : errorMessages) {
				globalMessage = globalMessage + "<p />"
						+ errorMessage.getDate() + " --> "
						+ errorMessage.getMessage();
			}
			// send the email with all message
			emailUtils.sendEmail(globalMessage);

			// set all sent message to checked
			errorMessageService.setChecked(errorMessages);

		} catch (ServiceException | MessagingException | IOException e) {
			logger.error("An error has occured " + e.getMessage());
			throw new ServiceException("error send emailtoAdmin", e);

		}
	}

	private Map<String, Integer> buildMapError(Map<String, Integer> map,
			DockerContainer container) {
		if (map.containsKey(container.getName())) {
			Integer numberOfViews = map.get(container.getName());
			map.remove(container.getName());
			map.put(container.getName(), numberOfViews + 1);
		} else {
			map.put(container.getName(), 1);
		}

		return map;
	}

	private ErrorMessage buildNewErrorMessage(String message) {
		ErrorMessage errorMessage = new ErrorMessage();
		errorMessage.setDate(new Date());
		errorMessage.setMessage(message);
		return errorMessage;
	}

	private DockerContainer buildContainerName(Server server) {
		logger.info(ApplicationEntryPoint.INSTANCE+"-"+server.getName());
		return new DockerContainerBuilder().withName(server.getName()).build();
	}

	private DockerContainer buildContainerName(Module module) {
		logger.info(ApplicationEntryPoint.INSTANCE+"-"+module.getName());
		return new DockerContainerBuilder().withName(module.getName()).build();
	}

}
