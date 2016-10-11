package fr.treeptik.cloudunit.service.impl;


import javax.inject.Inject;

import org.springframework.stereotype.Service;

import fr.treeptik.cloudunit.dao.VolumeAssociationDAO;
import fr.treeptik.cloudunit.exception.ServiceException;
import fr.treeptik.cloudunit.model.VolumeAssociation;
import fr.treeptik.cloudunit.service.VolumeAssociationService;


@Service
public class VolumeAssociationServiceImpl implements VolumeAssociationService {

@Inject
private VolumeAssociationDAO volumeAssociationDAO;


	@Override
	public Integer checkVolumeAssociationPathAlreadyPresent(String path, int id) throws ServiceException {
		return volumeAssociationDAO.countVolumeAssociationByPathAndServer(path, id);
	}

}
