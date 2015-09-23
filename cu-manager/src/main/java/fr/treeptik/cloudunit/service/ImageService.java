package fr.treeptik.cloudunit.service;

import fr.treeptik.cloudunit.exception.ServiceException;
import fr.treeptik.cloudunit.model.Image;

import java.util.List;

public interface ImageService {

	Image update(Image image) throws ServiceException;

	Image create(Image image) throws ServiceException;

	void remove(Image image) throws ServiceException;

	Image findById(Integer id) throws ServiceException;

	List<Image> findAll() throws ServiceException;

	Image findByName(String name) throws ServiceException;

	Image enableImage(String imageName) throws ServiceException;

	Image disableImage(String imageName) throws ServiceException;

	List<Image> findEnabledImages() throws ServiceException;

    List<Image> findEnabledImagesByType(String type) throws ServiceException;

	Long countNumberOfInstances(String moduleName, String applicationName,
			String userLogin) throws ServiceException;

}
