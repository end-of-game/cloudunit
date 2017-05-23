package fr.treeptik.cloudunit.orchestrator.docker.service.impl;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Optional;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.treeptik.cloudunit.orchestrator.core.Container;
import fr.treeptik.cloudunit.orchestrator.core.Image;
import fr.treeptik.cloudunit.orchestrator.docker.repository.ImageRepository;
import fr.treeptik.cloudunit.orchestrator.docker.service.DockerService;
import fr.treeptik.cloudunit.orchestrator.docker.service.FileService;
import fr.treeptik.cloudunit.orchestrator.docker.service.ServiceException;
import fr.treeptik.cloudunit.orchestrator.docker.service.impl.DockerServiceImpl.ExecutionResult;

@Component
public class FileServiceImpl implements FileService {

	private static final Logger LOGGER = LoggerFactory.getLogger(FileServiceImpl.class);

	@Autowired
	private DockerService dockerService;

	@Autowired
	private ImageRepository imageRepository;

	@Override
	public void deploy(Container container, String fileUri, String contextPath) {
		Optional<Image> image = imageRepository.findByRepositoryTag(container.getImageName());
		if (!image.isPresent()) {
			throw new ServiceException(String.format("Cannot deploy archive on container %s", container.getName()));
		}

		String containerId = container.getName();
		String destination = image.get().getTempFolder();
		String filePath = String.format("%s/%s", destination, FilenameUtils.getName(fileUri));
		contextPath = String.format("/%s", contextPath);
		String deployCmd = MessageFormat.format(image.get().getDeployCmd(), filePath, contextPath);

		sendFileToContainer(containerId, fileUri, destination);
		ExecutionResult execute = dockerService.execute(container, deployCmd.split(" "));
		LOGGER.debug(execute.output);
	}

	@Override
	public void sendFileToContainer(String containerId, String fileUri, String destination) {
		try {
			File file = null;
			File createTempHomeDirPerUsage = null;
			File homeDirectory = null;
			try {
				homeDirectory = FileUtils.getUserDirectory();
				createTempHomeDirPerUsage = new File(
						homeDirectory.getAbsolutePath() + "/tmp" + System.currentTimeMillis());
				if (createTempHomeDirPerUsage.mkdirs()) {
					if (fileUri != null) {
						String fileName = FilenameUtils.getName(fileUri);
						fileName = fileName.replace(" ", "_");
						file = new File(createTempHomeDirPerUsage.getAbsolutePath() + "/" + fileName);
						FileUtils.copyURLToFile(new URL(fileUri), file);
					}
					dockerService.sendFileToContainer(containerId, file.getParent(), file.getName(), destination);
				} else {
					throw new ServiceException("Cannot create : " + createTempHomeDirPerUsage.getAbsolutePath());
				}
			} finally {
				if (createTempHomeDirPerUsage != null) {
					boolean deleted = file.delete();
					LOGGER.debug(file.getAbsolutePath() + " is deleted ? " + deleted);
					deleted = createTempHomeDirPerUsage.delete();
					LOGGER.debug(createTempHomeDirPerUsage.getAbsolutePath() + " is deleted ? " + deleted);
				}
			}
		} catch (IOException e) {
			throw new ServiceException("Cannot store the file from " + fileUri);
		}
	}
}
