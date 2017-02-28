package fr.treeptik.cloudunit.domain.service;

import fr.treeptik.cloudunit.domain.core.Application;
import fr.treeptik.cloudunit.domain.core.Service;

public interface ApplicationService {
    public Application create(String name);

    public void delete(Application application);

    public Service addService(Application application, String imageName);
}
