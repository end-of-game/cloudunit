package fr.treeptik.cloudunit.domain.service.impl;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import fr.treeptik.cloudunit.domain.service.ServiceException;
import fr.treeptik.cloudunit.domain.service.StorageService;

@Component
public class StorageServiceImpl implements StorageService {

	private static final Logger LOGGER = LoggerFactory.getLogger(StorageServiceImpl.class);

	public File store(MultipartFile fileUpload) {
		try {
			String tempDirectoryPath = System.getProperty("java.io.tmpdir");
			File file = new File(String.format("%s/%s",tempDirectoryPath, fileUpload.getOriginalFilename()));
			fileUpload.transferTo(file);
			return file;
		} catch (IllegalStateException | IOException e) {
			LOGGER.error("Couldn't store file {}", fileUpload.getOriginalFilename());
			throw new ServiceException(e.getMessage(), e);
		}
	}

	@Override
	public File findByName(String fileName) {
		String tempDirectoryPath = System.getProperty("java.io.tmpdir");
		File file = new File(String.format("%s/%s",tempDirectoryPath, fileName));
		LOGGER.info(tempDirectoryPath);
		if (!file.exists()) {
			throw new ServiceException(String.format("Couldn't find file %s", fileName));
		}
		return file;
	}

}
