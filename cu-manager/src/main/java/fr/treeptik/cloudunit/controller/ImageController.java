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

package fr.treeptik.cloudunit.controller;

import java.util.List;

import javax.inject.Inject;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import fr.treeptik.cloudunit.exception.ServiceException;
import fr.treeptik.cloudunit.model.Image;
import fr.treeptik.cloudunit.model.User;
import fr.treeptik.cloudunit.service.ImageService;
import fr.treeptik.cloudunit.utils.AuthentificationUtils;

@Controller
@RequestMapping("/image")
public class ImageController {
    @Inject
    private ImageService imageService;

    @Inject
    private AuthentificationUtils authentificationUtils;

    @GetMapping(value = "/all")
    public
    @ResponseBody
    List<Image> listAllImages()
        throws ServiceException {
        return imageService.findAll();
    }

    @RequestMapping(value = "/module/enabled", method = RequestMethod.GET)
    public
    @ResponseBody
    List<Image> listAllEnabledModuleImages()
        throws ServiceException {
        return imageService.findEnabledImagesByType("module");
    }

    @RequestMapping(value = "/server/enabled", method = RequestMethod.GET)
    public
    @ResponseBody
    List<Image> listAllEnabledServerImages()
        throws ServiceException {
        return imageService.findEnabledImagesByType("server");
    }

    @RequestMapping(value = "/{imageName}/enabled", method = RequestMethod.PUT)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void enableImage(@PathVariable String imageName) throws ServiceException {
        imageService.enableImage(imageName);
    }

    @RequestMapping(value = "/{imageName}/disabled", method = RequestMethod.PUT)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void disableImage(@PathVariable String imageName) throws ServiceException {
        imageService.disableImage(imageName);
    }

    @RequestMapping(value = "/{imageId}", method = RequestMethod.DELETE)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteImage(@PathVariable Integer imageId) throws ServiceException {
        imageService.delete(imageId);
    }

    @RequestMapping(value = "/pull", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void pullImage( @RequestBody Image image) throws ServiceException {
        User user = authentificationUtils.getAuthentificatedUser();
        authentificationUtils.forbidUser(user);
        try {
            imageService.pull(image.getPath());
        } finally {
            authentificationUtils.allowUser(user);
        }

    }

}
