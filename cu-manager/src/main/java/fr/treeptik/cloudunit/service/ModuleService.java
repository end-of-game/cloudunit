package fr.treeptik.cloudunit.service;

import fr.treeptik.cloudunit.exception.CheckException;
import fr.treeptik.cloudunit.exception.ServiceException;
import fr.treeptik.cloudunit.model.Application;
import fr.treeptik.cloudunit.model.Module;
import fr.treeptik.cloudunit.model.Status;
import fr.treeptik.cloudunit.model.User;

import java.io.File;
import java.util.List;

public interface ModuleService {

	List<Module> findAll() throws ServiceException;

	Module findById(Integer id) throws ServiceException;

	Module findByName(String moduleName) throws ServiceException;

	Module update(Module module) throws ServiceException;

	List<Module> findByAppAndUser(User user, String applicationName)
			throws ServiceException;

	void checkImageExist(String moduleName) throws ServiceException;

	void initDb(User user, String applicationName, final String moduleName,
			File file) throws ServiceException;

	Module saveInDB(Module module) throws ServiceException;

	List<Module> findAllStatusStopModules() throws ServiceException;

	List<Module> findAllStatusStartModules() throws ServiceException;

	List<Module> findByApp(Application application) throws ServiceException;

	Module startModule(Module module) throws ServiceException;

	Module stopModule(Module module) throws ServiceException;

	void checkStatus(Module module, String status) throws CheckException,
			ServiceException;

	boolean checkStatusPENDING(Module module) throws ServiceException;

	Module findByContainerID(String id) throws ServiceException;

	void addModuleManager(Module module, Long instanceNumber)
			throws ServiceException;

	Module remove(Application application, User user, Module module,
				  Boolean isModuleRemoving, Status previousApplicationStatus)
			throws ServiceException, CheckException;

	Module restoreBackup(String moduleName) throws ServiceException;

	Module initModule(Application application, Module module, String tag)
			throws ServiceException, CheckException;

	Module findGitModule(String login, Application application)
			throws ServiceException;

}
