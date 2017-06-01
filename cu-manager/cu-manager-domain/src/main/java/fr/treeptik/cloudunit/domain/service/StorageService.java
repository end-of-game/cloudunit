package fr.treeptik.cloudunit.domain.service;

import java.io.InputStream;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {

	String store(MultipartFile fileUpload);

	InputStream findById(String fileName);

}
