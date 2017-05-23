package fr.treeptik.cloudunit.domain.service;

import java.io.File;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {

	File store(MultipartFile file, String path);

	File store(MultipartFile fileUpload);

}
