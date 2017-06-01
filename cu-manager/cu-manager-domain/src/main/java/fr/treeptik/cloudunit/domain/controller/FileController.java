package fr.treeptik.cloudunit.domain.controller;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import fr.treeptik.cloudunit.domain.service.StorageService;

@Controller
@RequestMapping("/files")
public class FileController {

	@Autowired
	private StorageService storageService;

	@GetMapping("/{fileId}")
	public void findByName(@PathVariable String fileId, HttpServletResponse response) throws IOException {
		InputStream in = storageService.findById(fileId);
		FileCopyUtils.copy(in, response.getOutputStream());
	}
	
	// TODO just to test, to remove
	@PostMapping
	public ResponseEntity<?> upload(@RequestPart MultipartFile file) {
		String id = storageService.store(file);
		return ResponseEntity.ok(id);
	}

}
