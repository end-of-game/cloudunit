package fr.treeptik.cloudunit.domain.service.impl;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import fr.treeptik.cloudunit.domain.service.ServiceException;
import fr.treeptik.cloudunit.domain.service.StorageService;

@Component
public class StorageServiceImpl implements StorageService {

	private static final Logger LOGGER = LoggerFactory.getLogger(StorageServiceImpl.class);

	@Override
	public File store(MultipartFile fileUpload, String path) {
		if (path == null) {
			path = FileUtils.getUserDirectoryPath() + "/storage/" + fileUpload.getOriginalFilename();
		}
		File file = new File(path);
		try {
			fileUpload.transferTo(file);
		} catch (IllegalStateException | IOException e) {
			LOGGER.error("Cannot store the file {}", fileUpload.getOriginalFilename());
		}
		return file;
	}
	
	@Override
	public File store(MultipartFile fileUpload) {
		File file = new File(FileUtils.getUserDirectoryPath() + "/storage/" + fileUpload.getOriginalFilename());
		try {
			fileUpload.transferTo(file);
		} catch (IllegalStateException | IOException e) {
			LOGGER.error("Cannot store the file {}", fileUpload.getOriginalFilename());
			throw new ServiceException(e.getMessage(), e);
		}
		return file;
	}

}
