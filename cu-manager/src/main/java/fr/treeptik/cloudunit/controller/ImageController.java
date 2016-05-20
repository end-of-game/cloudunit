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

import fr.treeptik.cloudunit.dto.ImageList;
import fr.treeptik.cloudunit.exception.ServiceException;
import fr.treeptik.cloudunit.model.Image;
import fr.treeptik.cloudunit.service.ImageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Controller
@RequestMapping("/image")
public class ImageController {

    @Inject
    private ImageService imageService;

    @Value("${api.version}")
    private String apiVersion;

    @RequestMapping(value = "/all", method = RequestMethod.GET)
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

    @RequestMapping(value = "/server/selection", method = RequestMethod.GET)
    public
    @ResponseBody
    Map<String, ImageList> listServerSelection()
            throws ServiceException {

        Map<String, ImageList> imageLists = new TreeMap<>();

        ImageList imageListTomcat = new ImageList();
        imageListTomcat.add("tomcat-8.0.35", true);
        imageListTomcat.add("tomcat-8.0.21", false);
        imageLists.put("tomcat", imageListTomcat);

        ImageList imageListJBoss = new ImageList();
        imageListJBoss.add("jboss-8.0", false);
        imageListJBoss.add("jboss-10", true);
        imageLists.put("jboss", imageListJBoss);

        return imageLists;
    }

    @RequestMapping(value = "/version", method = RequestMethod.GET)
    public
    @ResponseBody
    String getVersion() {
        return apiVersion;
    }

}
