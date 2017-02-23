package fr.treeptik.cloudunit.domain.repository;

import java.util.Optional;

import org.springframework.data.repository.Repository;

import fr.treeptik.cloudunit.domain.model.Image;

public interface ImageRepository extends Repository<Image, String> {
    Optional<Image> findOne(String id);
    Optional<Image> findByName(String name);
}
