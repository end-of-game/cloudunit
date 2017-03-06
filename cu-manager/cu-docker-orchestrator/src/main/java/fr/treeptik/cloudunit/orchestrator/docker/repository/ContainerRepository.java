package fr.treeptik.cloudunit.orchestrator.docker.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.Repository;

import fr.treeptik.cloudunit.orchestrator.core.Container;

public interface ContainerRepository extends Repository<Container, String> {
    Optional<Container> findOne(String id);
    Optional<Container> findByName(String name);
    Optional<Container> findByContainerId(String containerId);
    
    List<Container> findAll();
    
    Container save(Container container);
    
    void delete(Container container);
}
