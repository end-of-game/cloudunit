package fr.treeptik.cloudunit.domain.service;

import org.springframework.web.multipart.MultipartFile;

import com.mongodb.gridfs.GridFSDBFile;

public interface StorageService {

	String store(MultipartFile fileUpload);

	GridFSDBFile findById(String fileName);

}
