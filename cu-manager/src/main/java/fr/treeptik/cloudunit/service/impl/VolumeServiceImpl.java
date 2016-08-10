package fr.treeptik.cloudunit.service.impl;

import fr.treeptik.cloudunit.dao.VolumeDAO;
import fr.treeptik.cloudunit.exception.ServiceException;
import fr.treeptik.cloudunit.model.Volume;
import fr.treeptik.cloudunit.service.VolumeService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;

@Service
public class VolumeServiceImpl implements VolumeService {

    @Inject
    private VolumeDAO volumeDAO;

    @Override
    public void save(Volume volume) throws ServiceException {
        volumeDAO.save(volume);
    }

    @Override
    public Volume loadVolume(int id) throws ServiceException {
        Volume volume = volumeDAO.findById(id);

        return volume;
    }

    @Override
    public List<Volume> loadVolumeByApplication(String applicationName) throws ServiceException {
        List<Volume> volumeList = volumeDAO.findByApplicationName(applicationName);

        return volumeList;
    }

    @Override
    public List<Volume> loadAllVolumes() throws ServiceException {
        List<Volume> volumeList = volumeDAO.findAllVolumes();

        return volumeList;
    }

    @Override
    public void delete(int id) throws ServiceException {
        volumeDAO.delete(id);
    }
}
