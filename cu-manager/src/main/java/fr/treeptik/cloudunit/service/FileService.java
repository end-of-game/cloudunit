package fr.treeptik.cloudunit.service;

import fr.treeptik.cloudunit.exception.ServiceException;
import fr.treeptik.cloudunit.json.ui.FileUnit;
import fr.treeptik.cloudunit.json.ui.SourceUnit;

import java.io.File;
import java.util.List;
import java.util.Optional;

/**
 * Created by nicolas on 20/05/15.
 */
public interface FileService {

	List<FileUnit> listByContainerIdAndPath(String containerId, String path)
			throws ServiceException;

	List<SourceUnit> listLogsFilesByContainer(String containerId)
			throws ServiceException;

	void sendFileToContainer(String applicationName, String containerId,
			File file, String originalName, String destFile)
			throws ServiceException;

	Optional<File> getFileFromContainer(String applicationName, String containerId,
			File file, String originalName, String destFile) throws ServiceException;

	void deleteFilesFromContainer(String applicationName, String containerId, String path) throws ServiceException;
	
}
