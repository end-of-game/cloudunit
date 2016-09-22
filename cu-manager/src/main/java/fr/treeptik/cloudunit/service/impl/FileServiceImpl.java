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

package fr.treeptik.cloudunit.service.impl;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

import javax.inject.Inject;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import fr.treeptik.cloudunit.dto.FileUnit;
import fr.treeptik.cloudunit.dto.LogResource;
import fr.treeptik.cloudunit.dto.SourceUnit;
import fr.treeptik.cloudunit.enums.RemoteExecAction;
import fr.treeptik.cloudunit.exception.CheckException;
import fr.treeptik.cloudunit.exception.FatalDockerJSONException;
import fr.treeptik.cloudunit.exception.ServiceException;
import fr.treeptik.cloudunit.filters.explorer.ExplorerFactory;
import fr.treeptik.cloudunit.filters.explorer.ExplorerFilter;
import fr.treeptik.cloudunit.service.DockerService;
import fr.treeptik.cloudunit.service.FileService;
import fr.treeptik.cloudunit.utils.AlphaNumericsCharactersCheckUtils;
import fr.treeptik.cloudunit.utils.FilesUtils;

/**
 * Service for file management into container Created by nicolas on 20/05/15.
 */
@Service
public class FileServiceImpl implements FileService {

	private Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);

	@Inject
	private DockerService dockerService;

	@Value("${docker.manager.ip:192.168.50.4:2376}")
	private String dockerManagerIp;

	@Value("${certs.dir.path}")
	private String certsDirPath;

	@Value("${docker.endpoint.mode}")
	private String dockerEndpointMode;

	/**
	 * File Explorer Feature
	 * <p>
	 * Delete all resources (files and folders) for an application + container +
	 * path.
	 *
	 * @param applicationName
	 * @param containerId
	 * @param path
	 * @throws ServiceException
	 */
	public void deleteFilesFromContainer(String applicationName, String containerId, String path)
			throws ServiceException {
		try {
			final String command = "rm -rf " + path;
			dockerService.execCommand(containerId, command);
		} catch (FatalDockerJSONException e) {
			throw new ServiceException("Cannot delete files " + path + " for " + containerId, e);
		}
	}

	@Override
	public void createDirectory(String applicationName, String containerId, String path) throws ServiceException {
		try {
			final String command = "mkdir -p " + path;
			dockerService.execCommand(containerId, command);
		} catch (FatalDockerJSONException e) {
			throw new ServiceException("Cannot create directory " + path + " for " + containerId, e);
		}
	}

	/**
	 * Logs Display Feature
	 * <p>
	 * List the files into the Log directory
	 *
	 * @param containerId
	 * @return
	 * @throws ServiceException
	 */
	public List<SourceUnit> listLogsFilesByContainer(String containerId) throws ServiceException {
		List<SourceUnit> files = new ArrayList<>();
		try {
			String logDirectory = getLogDirectory(containerId);
			String containerName = dockerService.getContainerNameFromId(containerId);
			final String command = "find " + logDirectory + " -type f ! -size 0 ";
			String execOutput = dockerService.execCommand(containerName, command);
			if (execOutput != null && execOutput.contains("cannot access") == false) {
				if (logger.isDebugEnabled()) {
					logger.debug(execOutput);
				}
				StringTokenizer lignes = new StringTokenizer(execOutput, "\n");
				while (lignes.hasMoreTokens()) {
					String name = lignes.nextToken();
					name = name.substring(name.lastIndexOf("/") + 1);
					SourceUnit sourceUnit = new SourceUnit(name);
					files.add(sourceUnit);
				}
			}
		} catch (FatalDockerJSONException e) {
			throw new ServiceException("Error in listByContainerIdAndPath", e);
		}
		return files;
	}

	/**
	 * Logs Display Feature
	 * <p>
	 * List the files and folder for a container
	 *
	 * @param containerId
	 * @return
	 * @throws ServiceException
	 */
	public String tailFile(String containerId, String filename, Integer maxRows) throws ServiceException {
		String execOutput = "";
		try {
			String logDir = getLogDirectory(containerId);
			if (!logDir.endsWith("/")) {
				logDir = logDir + "/";
			}
			final String command = "tail -n " + maxRows + " " + logDir + filename;
			execOutput = dockerService.execCommand(containerId, command);
			if (execOutput != null && execOutput.contains("cannot access") == false) {
				return execOutput;
			}
		} catch (FatalDockerJSONException e) {
			StringBuilder builder = new StringBuilder(256);
			builder.append("containerId=").append(containerId);
			builder.append(",file=").append(filename);
			builder.append(",nbRows=").append(maxRows);
			throw new ServiceException(builder.toString(), e);
		}
		return execOutput;
	}

	/**
	 * File Explorer Feature
	 * <p>
	 * List the files by Container and Path
	 *
	 * @param containerId
	 * @param path
	 * @return
	 * @throws ServiceException
	 */
	public List<FileUnit> listByContainerIdAndPath(String containerId, String path) throws ServiceException {
		List<FileUnit> files = new ArrayList<>();
		try {
			final String command = "ls -laF " + path;
			String execOutput = dockerService.execCommand(containerId, command);
			String containerName = dockerService.getContainerNameFromId(containerId);
			ExplorerFilter filter = ExplorerFactory.getInstance().getCustomFilter(containerName);
			if (execOutput != null && execOutput.contains("cannot access") == false) {

				if (logger.isDebugEnabled()) {
					logger.debug(execOutput);
				}

				StringTokenizer lignes = new StringTokenizer(execOutput, "\n");
				while (lignes.hasMoreTokens()) {
					String ligne = lignes.nextToken();
					if (logger.isDebugEnabled()) {
						logger.debug(ligne);
					}
					if (ligne.startsWith("total"))
						continue;
					StringTokenizer fields = new StringTokenizer(ligne, " ");
					String rights = fields.nextToken();
					String id = fields.nextToken();
					String user = fields.nextToken();
					String group = fields.nextToken();
					String size = fields.nextToken();
					String month = fields.nextToken();
					String day = fields.nextToken();
					String hour = fields.nextToken();
					String name = fields.nextToken();
					boolean dir = false;
					boolean exec = false;
					if (name.endsWith("/")) {
						dir = true;
						name = name.substring(0, name.length() - 1);
					} else {
						boolean isNotAuth = FilesUtils.isNotAuthorizedExtension(name);
						if (isNotAuth) {
							continue;
						}
					}
					if (name.endsWith("*")) {
						exec = true;
						name = name.substring(0, name.length() - 1);
					}
					StringBuilder absolutePath = new StringBuilder(128);
					absolutePath.append(path).append(name);

					if (name.equalsIgnoreCase("."))
						continue;
					if (name.equalsIgnoreCase(".."))
						continue;

					FileUnit fileUnit = new FileUnit(name, user, day, month, hour, false, dir, exec,
							absolutePath.toString());

					if (filter.isValid(fileUnit)) {
						filter.isRemovable(fileUnit);
						filter.isSafe(fileUnit);
						files.add(fileUnit);
					}
				}
			}
		} catch (FatalDockerJSONException e) {
			throw new ServiceException(containerId, e);
		}

		return files;
	}

	/**
	 * File Explorer feature
	 * <p>
	 * Send a file into a container
	 *
	 * @param containerId
	 * @Param destination
	 * @throws ServiceException
	 */
	@Override
	public void sendFileToContainer(String containerId, String destination, MultipartFile fileUpload,
			String contentFileName, String contentFileData) throws ServiceException, CheckException {
		try {
			File file = null;
			File createTempHomeDirPerUsage = null;
			File homeDirectory = null;
			try {
				homeDirectory = org.apache.commons.io.FileUtils.getUserDirectory();
				createTempHomeDirPerUsage = new File(
						homeDirectory.getAbsolutePath() + "/tmp" + System.currentTimeMillis());
				if (createTempHomeDirPerUsage.mkdirs()) {
					String fileName = null;
					// usecase : upload a file
					if (fileUpload != null) {
						if (contentFileName == null) {
							fileName = fileUpload.getOriginalFilename();
							fileName = AlphaNumericsCharactersCheckUtils.deAccent(fileName);
							fileName = fileName.replace(" ", "_");
						} else {
							fileName = contentFileName;
						}
						file = new File(createTempHomeDirPerUsage.getAbsolutePath() + "/" + fileName);
						fileUpload.transferTo(file);
					}
					// usecase : save the content file
					else {
						fileName = contentFileName;
						file = new File(createTempHomeDirPerUsage.getAbsolutePath() + "/" + contentFileName);
						FileUtils.write(file, contentFileData);
					}
					dockerService.sendFileToContainer(containerId, file.getParent(), fileName, destination);
				} else {
					throw new ServiceException("Cannot create : " + createTempHomeDirPerUsage.getAbsolutePath());
				}
			} finally {
				if (createTempHomeDirPerUsage != null) {
					boolean deleted = file.delete();
					logger.debug(file.getAbsolutePath() + " is deleted ? " + deleted);
					deleted = createTempHomeDirPerUsage.delete();
					logger.debug(createTempHomeDirPerUsage.getAbsolutePath() + " is deleted ? " + deleted);
				}
			}
			if (destination.contains("/opt/cloudunit")) {
				dockerService.execCommand(containerId, RemoteExecAction.CHANGE_CU_RIGHTS.getCommand(), true);
			}
		} catch (FatalDockerJSONException | IOException e) {
			StringBuilder msgError = new StringBuilder(512);
			msgError.append(",").append("containerId=").append(containerId);
			msgError.append(",").append("fileUpload=").append(fileUpload);
			msgError.append(",").append("destFile=").append(destination);
			throw new ServiceException("error in send file into the container : " + msgError, e);
		}

	}

	/**
	 * File Explorer feature
	 * <p>
	 * Gather a file from a container
	 *
	 * @param containerId
	 * @return
	 * @throws ServiceException
	 */
	@Override
	public int getFileFromContainer(String containerId, String pathFile, OutputStream outputStream)
			throws ServiceException {
		try {
			return dockerService.getFileFromContainer(containerId, pathFile, outputStream);
		} catch (FatalDockerJSONException e) {
			StringBuilder msgError = new StringBuilder();
			msgError.append(", containerId=").append("=").append(containerId);
			throw new ServiceException(msgError.toString(), e);
		}
	}

	public String getLogDirectory(String containerId) throws ServiceException {
		String location = null;
		try {
			location = dockerService.getEnv(containerId, "CU_LOGS");
		} catch (Exception e) {
			logger.error(containerId, e);
		}
		return location;
	}

}
