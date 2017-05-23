package fr.treeptik.cloudunit.orchestrator.docker.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.Repository;

import fr.treeptik.cloudunit.orchestrator.core.Image;

public interface ImageRepository extends Repository<Image, String> {
    Optional<Image> findOne(String id);
    Optional<Image> findByName(String name);
    Optional<Image> findByRepositoryTag(String repositoryTag);
    
    List<Image> findAll();
    
    Image save(Image image);
    
    void delete(Image image);
    void deleteAll();

}
