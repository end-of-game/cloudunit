package fr.treeptik.cloudunit.domain.service;

import java.util.Optional;

import fr.treeptik.cloudunit.domain.model.Image;

public interface ImageService {

    Optional<Image> findByName(String imageType);

}
