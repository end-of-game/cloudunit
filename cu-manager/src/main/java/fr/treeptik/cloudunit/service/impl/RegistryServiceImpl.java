package fr.treeptik.cloudunit.service.impl;

import fr.treeptik.cloudunit.dao.RegistryDAO;
import fr.treeptik.cloudunit.exception.ServiceException;
import fr.treeptik.cloudunit.model.Registry;
import fr.treeptik.cloudunit.service.RegistryService;

import org.springframework.stereotype.Service;
import javax.inject.Inject;
import java.util.List;

@Service
public class RegistryServiceImpl implements RegistryService {

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
}
