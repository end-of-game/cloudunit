package fr.treeptik.cloudunit.orchestrator.docker.service.impl;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import fr.treeptik.cloudunit.orchestrator.core.Container;
import fr.treeptik.cloudunit.orchestrator.core.Image;
import fr.treeptik.cloudunit.orchestrator.docker.repository.ImageRepository;
import fr.treeptik.cloudunit.orchestrator.docker.service.DockerService;
import fr.treeptik.cloudunit.orchestrator.docker.service.FileService;
import fr.treeptik.cloudunit.orchestrator.docker.service.ServiceException;

@Component
public class FileServiceImpl implements FileService {

	private static final Logger LOGGER = LoggerFactory.getLogger(FileServiceImpl.class);

	@Autowired
	private DockerService dockerService;
	
	@Autowired
	private ImageRepository imageRepository;
	
	@Override
	public void deploy(Container container, MultipartFile fileUpload) {
		Optional<Image> image = imageRepository.findByRepositoryTag(container.getImageName());
		if(!image.isPresent()) {
			throw new ServiceException(String.format("Cannot deploy archive on container %s", container.getName()));
		}
		String destination = image.get().getTempFolder();
		sendFileToContainer(container, destination, fileUpload);
		// TODO run deploy.sh
	}

	@Override
	public void sendFileToContainer(Container container, String destination, MultipartFile fileUpload) {
		String containerId = container.getName();
		try {
			File file = null;
			File createTempHomeDirPerUsage = null;
			File homeDirectory = null;
			try {
				homeDirectory = FileUtils.getUserDirectory();
				createTempHomeDirPerUsage = new File(
						homeDirectory.getAbsolutePath() + "/tmp" + System.currentTimeMillis());
				if (createTempHomeDirPerUsage.mkdirs()) {
					String fileName = null;
					if (fileUpload != null) {
						fileName = fileUpload.getOriginalFilename();
						fileName = fileName.replace(" ", "_");
						file = new File(createTempHomeDirPerUsage.getAbsolutePath() + "/" + fileName);
						fileUpload.transferTo(file);
					}
					dockerService.sendFileToContainer(containerId, file.getParent(), fileName, destination);
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
			StringBuilder msgError = new StringBuilder(512);
			msgError.append(",").append("containerId=").append(containerId);
			msgError.append(",").append("fileUpload=").append(fileUpload);
			msgError.append(",").append("destFile=").append(destination);
			throw new ServiceException("error in send file into the container : " + msgError, e);
		}

	}
}
