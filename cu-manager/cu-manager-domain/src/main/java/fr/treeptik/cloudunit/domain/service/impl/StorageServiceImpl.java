package fr.treeptik.cloudunit.domain.service.impl;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSFile;

import fr.treeptik.cloudunit.domain.service.ServiceException;
import fr.treeptik.cloudunit.domain.service.StorageService;

@Component
public class StorageServiceImpl implements StorageService {

	@Autowired
	private GridFsTemplate gridFsTemplate;

	@Override
	public String store(MultipartFile file) {
		String name = file.getOriginalFilename();
		try {
			GridFSFile gridFsFile = gridFsTemplate.store(file.getInputStream(), name, file.getContentType());
			gridFsFile.save();
			return gridFsFile.getId().toString();
		} catch (IOException e) {
			throw new ServiceException(String.format("Couldn't store the file %s", name), e);
		}
	}

	@Override
	public InputStream findById(String id) {
		GridFSDBFile file = gridFsTemplate.findOne(new Query(Criteria.where("_id").is(id)));
		if (file == null) {
			throw new ServiceException(String.format("Couldn't find file with id : %s", id));
		}
		return file.getInputStream();
	}

}
