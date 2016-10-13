package fr.treeptik.cloudunit.service;

import java.util.List;

import fr.treeptik.cloudunit.exception.ServiceException;
import fr.treeptik.cloudunit.model.VolumeAssociation;

public interface VolumeAssociationService {
	Integer checkVolumeAssociationPathAlreadyPresent(String path, int id) throws ServiceException;
}
