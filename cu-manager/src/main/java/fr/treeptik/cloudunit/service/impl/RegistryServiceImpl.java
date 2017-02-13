package fr.treeptik.cloudunit.service.impl;

import fr.treeptik.cloudunit.dao.RegistryDAO;
import fr.treeptik.cloudunit.exception.CheckException;
import fr.treeptik.cloudunit.exception.ServiceException;
import fr.treeptik.cloudunit.model.Registry;
import fr.treeptik.cloudunit.service.RegistryService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.inject.Inject;
import javax.persistence.PersistenceException;
import java.util.List;

@Service
public class RegistryServiceImpl implements RegistryService {

//    private Logger logger = LoggerFactory.getLogger(RegistryServiceImpl.class);

    @Inject
    private RegistryDAO registryDAO;

    @Override
    public List<Registry> findAll() throws ServiceException {
        return registryDAO.findAll();
    }

    @Override
    public Registry createNewRegistry(String endpoint, String username, String password, String email)
            throws ServiceException {
        Registry registry = new Registry(endpoint, username, password, email);
        registryDAO.save(registry);
        return registry;
    }

    @Override
    @Transactional(readOnly = true)
    public Registry loadRegistry(int id) throws CheckException {
        Registry registry = registryDAO.findById(id);
        if (registry == null)
            throw new CheckException("Registry doesn't exist");
        return registry;
    }

    @Override
    @Transactional
    public void deleteRegistry(Integer id) throws ServiceException {

        Registry registry = loadRegistry(id);
        registryDAO.delete(id);

    }
}
