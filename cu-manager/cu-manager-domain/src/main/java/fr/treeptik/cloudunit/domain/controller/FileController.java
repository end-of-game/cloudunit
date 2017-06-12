package fr.treeptik.cloudunit.domain.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.mongodb.gridfs.GridFSDBFile;

import fr.treeptik.cloudunit.domain.service.StorageService;

@Controller
@RequestMapping("/files")
public class FileController {

	@Autowired
	private StorageService storageService;

	@GetMapping("/{fileId}")
	public void findByName(@PathVariable String fileId, HttpServletResponse response) throws IOException {
		GridFSDBFile file = storageService.findById(fileId);
		response.setHeader("Content-Disposition", String.format("attachment; filename=%s", file.getFilename()));
		FileCopyUtils.copy(file.getInputStream(), response.getOutputStream());
	}

}
