package fr.treeptik.cloudunit.service;

import fr.treeptik.cloudunit.exception.ServiceException;
import fr.treeptik.cloudunit.model.Volume;

import java.util.List;

public interface VolumeService {
    void save(Volume volume) throws ServiceException;

    Volume loadVolume(int id) throws ServiceException;

    List<Volume> loadVolumeByApplication(String applicationName) throws ServiceException;

    List<Volume> loadAllVolumes() throws ServiceException;

    void delete(int id) throws ServiceException;
}
