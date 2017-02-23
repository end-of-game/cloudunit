package fr.treeptik.cloudunit.domain.service;

import fr.treeptik.cloudunit.domain.model.Application;

public interface ApplicationService {
    public Application create(String name);

    public void delete(Application application);
}
