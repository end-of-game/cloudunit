package fr.treeptik.cloudunit.orchestrator.docker.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.Repository;

import fr.treeptik.cloudunit.orchestrator.core.Volume;

public interface VolumeRepository extends Repository<Volume, String> {
    Optional<Volume> findOne(String id);
    Optional<Volume> findByName(String name);
    
    List<Volume> findAll();
    
    Volume save(Volume volume);
    
    void delete(Volume volume);
}
