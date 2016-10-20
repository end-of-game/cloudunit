/*
 * LICENCE : CloudUnit is available under the GNU Affero General Public License : https://gnu.org/licenses/agpl.html
 *     but CloudUnit is licensed too under a standard commercial license.
 *     Please contact our sales team if you would like to discuss the specifics of our Enterprise license.
 *     If you are not sure whether the GPL is right for you,
 *     you can always test our software under the GPL and inspect the source code before you contact us
 *     about purchasing a commercial license.
 *
 *     LEGAL TERMS : "CloudUnit" is a registered trademark of Treeptik and can't be used to endorse
 *     or promote products derived from this project without prior written permission from Treeptik.
 *     Products or services derived from this software may not be called "CloudUnit"
 *     nor may "Treeptik" or similar confusing terms appear in their names without prior written permission.
 *     For any questions, contact us : contact@treeptik.fr
 */
package fr.treeptik.cloudunit.cli.utils;

import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.treeptik.cloudunit.cli.CloudUnitCliException;
import fr.treeptik.cloudunit.cli.Guard;
import fr.treeptik.cloudunit.cli.exception.ManagerResponseException;
import fr.treeptik.cloudunit.cli.processor.InjectLogger;
import fr.treeptik.cloudunit.cli.rest.JsonConverter;
import fr.treeptik.cloudunit.cli.rest.RestUtils;
import fr.treeptik.cloudunit.model.Image;

@Component
public class CheckUtils {

	@InjectLogger
	private Logger log;

	@Autowired
	private RestUtils restUtils;

	@Autowired
	private AuthenticationUtils authentificationUtils;

	@Autowired
	private UrlLoader urlLoader;

	public boolean imageExists(String imageName) {
		List<Image> images = null;
		try {
			images = JsonConverter
					.getImages(restUtils.sendGetCommand(authentificationUtils.finalHost + urlLoader.imageFind + "/all",
							authentificationUtils.getMap()).get("body"));
		} catch (ManagerResponseException e) {
			throw new CloudUnitCliException("Couldn't check for image", e);
		}
		
		return images.stream()
		    .filter(i -> i.getName().equals(imageName))
		    .findAny()
		    .isPresent();		
	}
	
	public void checkImageExists(String imageName) {
	    Guard.guardTrue(imageExists(imageName), "No such image \"{0}\"", imageName);
	}
}
