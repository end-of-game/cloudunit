package fr.treeptik.cloudunit.domain.service.impl;

import java.util.Optional;

import org.springframework.stereotype.Component;

import fr.treeptik.cloudunit.domain.model.Image;
import fr.treeptik.cloudunit.domain.service.ImageService;

@Component
public class ImageServiceImpl implements ImageService {

    @Override
    public Optional<Image> findByName(String imageType) {
        // TODO Auto-generated method stub
        return null;
    }

}
