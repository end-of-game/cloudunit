/*
 * LICENCE : CloudUnit is available under the GNU Affero General Public License : https://gnu.org/licenses/agpl.html
 * but CloudUnit is licensed too under a standard commercial license.
 * Please contact our sales team if you would like to discuss the specifics of our Enterprise license.
 * If you are not sure whether the AGPL is right for you,
 * you can always test our software under the AGPL and inspect the source code before you contact us
 * about purchasing a commercial license.
 *
 * LEGAL TERMS : "CloudUnit" is a registered trademark of Treeptik and can't be used to endorse
 * or promote products derived from this project without prior written permission from Treeptik.
 * Products or services derived from this software may not be called "CloudUnit"
 * nor may "Treeptik" or similar confusing terms appear in their names without prior written permission.
 * For any questions, contact us : contact@treeptik.fr
 */

package fr.treeptik.cloudunit.service.impl;

import fr.treeptik.cloudunit.dao.ImageDAO;
import fr.treeptik.cloudunit.exception.ServiceException;
import fr.treeptik.cloudunit.model.Image;
import fr.treeptik.cloudunit.service.DockerService;
import fr.treeptik.cloudunit.service.ImageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.persistence.PersistenceException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ImageServiceImpl
        implements ImageService {
    private Logger logger = LoggerFactory.getLogger(ImageServiceImpl.class);

    @Inject
    private ImageDAO imageDAO;

    @Inject
    private DockerService dockerService;

    public ImageDAO getImageDAO() {
        return this.imageDAO;
    }

    @Override
    @Transactional
    public Image create(Image image)
            throws ServiceException {

        logger.debug("create : Methods parameters : " + image.toString());
        logger.info("ImageService : Starting creating image " + image.getName());

        try {
            imageDAO.save(image);
        } catch (PersistenceException e) {
            logger.error("ImageService Error : Create Image" + e);
            throw new ServiceException(e.getLocalizedMessage(), e);
        }

        logger.info("ImageService : Image " + image.getName()
                + "successfully created.");

        return image;
    }

    @Override
    @Transactional
    public Image update(Image image)
            throws ServiceException {

        logger.debug("update : Methods parameters : " + image.toString());
        logger.info("ImageService : Starting updating image " + image.getName());

        try {
            imageDAO.saveAndFlush(image);
        } catch (PersistenceException e) {
            logger.error("ImageService Error : update Image" + e);
            throw new ServiceException(e.getLocalizedMessage(), e);
        }

        logger.info("ImageService : Image " + image.getName()
                + "successfully updated.");

        return image;
    }

    @Override
    @Transactional
    public void remove(Image image)
            throws ServiceException {
        try {
            logger.debug("remove : Methods parameters : " + image.toString());
            logger.info("Starting removing application " + image.getName());

            imageDAO.delete(image);

            logger.info("ImageService : Image successfully removed ");

        } catch (PersistenceException e) {

            logger.error("ImageService Error : failed to remove "
                    + image.getName() + " : " + e);

            throw new ServiceException(e.getLocalizedMessage(), e);
        }
    }

    @Override
    public Image findById(Integer id)
            throws ServiceException {
        try {
            logger.debug("findById : Methods parameters : " + id);
            Image image = imageDAO.findOne(id);
            logger.info("image with id " + id + " found!");
            return image;
        } catch (PersistenceException e) {
            logger.error("Error ImageService : error findById Method : " + e);
            throw new ServiceException(e.getLocalizedMessage(), e);

        }
    }

    @Override
    public List<Image> findAll()
            throws ServiceException {
        try {
            logger.debug("start findAll");
            List<Image> images = imageDAO.findAll();
            images = this.checkImagesPulled(images);
            logger.info("ImageService : All Images found ");
            return images;
        } catch (PersistenceException e) {
            logger.error("Error ImageService : error findById Method : " + e);
            throw new ServiceException(e.getLocalizedMessage(), e);

        }
    }

    @Override
    public Image findByName(String name)
            throws ServiceException {
        try {
            logger.debug("findById : Methods parameters : " + name);
            Image image = imageDAO.findByName(name);
            logger.debug("image with id " + name + " found!");
            return image;
        } catch (Exception e) {
            logger.error("Image Name : " + name);
            throw new ServiceException(name, e);
        }
    }

    @Override
    @Transactional
    public Image enableImage(String imageName)
            throws ServiceException {
        Image image;
        try {
            image = this.findByName(imageName);

            image.setEnable(Image.ENABLED);
            image = this.update(image);
        } catch (ServiceException e) {
            throw new ServiceException(
                    "Error ImageService : error enable Image", e);
        }

        return image;
    }

    @Override
    @Transactional
    public Image disableImage(String imageName)
            throws ServiceException {
        Image image;
        try {
            image = this.findByName(imageName);
            image.setEnable(Image.DISABLED);
            image = this.update(image);
        } catch (ServiceException e) {
            throw new ServiceException(
                    "Error ImageService : error disable Image", e);
        }
        return image;
    }

    @Override
    public List<Image> findEnabledImages()
            throws ServiceException {
        try {
            logger.debug("start find enabled images");
            List<Image> images = imageDAO.findAllEnabledImages();
            images = this.checkImagesPulled(images);
            logger.info("ImageService : enabled found ");
            return images;
        } catch (PersistenceException e) {
            logger.error("Error ImageService : error find enabled images Method : "
                    + e);
            throw new ServiceException(e.getLocalizedMessage(), e);
        }
    }

    @Override
    public List<Image> findEnabledImagesByType(String type)
            throws ServiceException {
        try {
            if (logger.isDebugEnabled()) {
                logger.debug("start find enabled '" + type + "' images");
            }
            List<Image> images = imageDAO.findAllEnabledImagesByType(type);
            logger.info("ImageService : enabled found ");
            return images;
        } catch (PersistenceException e) {
            logger.error("Error ImageService : error find enabled images Method : "
                    + e);
            throw new ServiceException(e.getLocalizedMessage(), e);
        }
    }

    @Override
    public Long countNumberOfInstances(String moduleName,
                                       String applicationName, String userLogin, String cuInstanceName)
            throws ServiceException {
        try {
            return imageDAO.countNumberOfInstances(moduleName, applicationName,
                    userLogin, cuInstanceName);
        } catch (PersistenceException e) {
            logger.error("Error ImageService : error find number of images Method : "
                    + e);
            throw new ServiceException(e.getLocalizedMessage(), e);

        }
    }

    @Override
    public void delete(Integer imageId) throws ServiceException {
        Image image = imageDAO.findOne(imageId);
        dockerService.deleteImage(image.getPath());
        this.disableImage(image.getName());
    }

    @Override
    public void pull(String imageName) {
        dockerService.pullImage(imageName);
    }

    private List<Image> checkImagesPulled(List<Image> images) throws ServiceException {
        List<String> listImages = this.dockerService.listImages();
        for (String tag: listImages) {
            images = images.stream().map( image -> {
                if(tag.contains(image.getPath())) image.setPull(true);
             return image;
            }).collect(Collectors.toList());
        }
        return images;
    }
}
