package fr.treeptik.cloudunit.orchestrator.docker.service.impl;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.text.MessageFormat;
import java.util.Optional;

import org.apache.commons.io.FileUtils;
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
		String filePath = sendFileToContainer(containerId, fileUri, destination);
		
		contextPath = String.format("/%s", contextPath);
		String deployCmd = MessageFormat.format(image.get().getDeployCmd(), filePath, contextPath);
		ExecutionResult execute = dockerService.execute(container, deployCmd.split(" "));
		LOGGER.debug(execute.output);
	}

	@Override
	public String sendFileToContainer(String containerId, String fileUri, String destination) {
		try {
			File file = null;
			File tempDirectory = null;
			File homeDirectory = null;
			try {
				homeDirectory = FileUtils.getUserDirectory();
				tempDirectory = new File(String.format("%s/tmp%d", homeDirectory.getAbsolutePath(), System.currentTimeMillis()));
				if (tempDirectory.mkdirs()) {
					if (fileUri != null) {
						URL url = new URL(fileUri);
						URLConnection urlConnection = url.openConnection();
						String header = urlConnection.getHeaderField("Content-Disposition");
						String filename = header.split("attachment; filename=")[1];
						file = new File(String.format("%s/%s", tempDirectory.getAbsolutePath(), filename));
						FileUtils.copyURLToFile(new URL(fileUri), file);
					}
					dockerService.sendFileToContainer(containerId, file.getParent(), file.getName(), destination);
					return String.format("%s/%s", destination, file.getName());
				} else {
					throw new ServiceException(String.format("Cannot create : %s", tempDirectory.getAbsolutePath()));
				}
			} finally {
				if (tempDirectory != null) {
					boolean deleted = file.delete();
					LOGGER.debug(file.getAbsolutePath() + " is deleted ? " + deleted);
					deleted = tempDirectory.delete();
					LOGGER.debug(tempDirectory.getAbsolutePath() + " is deleted ? " + deleted);
				}
			}
		} catch (IOException e) {
			throw new ServiceException(String.format("Cannot store the file from %s", fileUri), e);
		}
	}
}
