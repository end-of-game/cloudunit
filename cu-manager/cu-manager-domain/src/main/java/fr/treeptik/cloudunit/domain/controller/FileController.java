package fr.treeptik.cloudunit.domain.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import fr.treeptik.cloudunit.domain.service.ServiceException;
import fr.treeptik.cloudunit.domain.service.StorageService;

@Controller
@RequestMapping("/files")
public class FileController {

	@Autowired
	private StorageService storageService;

	@GetMapping("/{filename}")
	public ResponseEntity<?> findByName(@PathVariable String filename, HttpServletResponse response) {
		File file = storageService.findByName(filename);
		InputStreamResource resource;
		try {
			resource = new InputStreamResource(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			throw new ServiceException(e.getMessage(), e);
		}

	    return ResponseEntity.ok()
	            .contentLength(file.length())
	            .contentType(MediaType.parseMediaType("application/octet-stream"))
	            .body(resource);
	}

}
