package fr.treeptik.cloudunit.service;

import fr.treeptik.cloudunit.exception.CheckException;
import fr.treeptik.cloudunit.exception.ServiceException;
import fr.treeptik.cloudunit.model.Snapshot;
import fr.treeptik.cloudunit.model.Status;
import fr.treeptik.cloudunit.model.User;

import java.util.List;

public interface SnapshotService {

	List<Snapshot> listAll(String login) throws ServiceException;

	Snapshot remove(String tag, String login) throws ServiceException,
			CheckException;

	Snapshot findOne(String tag, String login);

	Snapshot create(String applicationName, User user, String tag,
			String description, Status previousStatus) throws ServiceException;

	Snapshot cloneFromASnapshot(String applicationName, String tag)
			throws ServiceException, InterruptedException;

}
