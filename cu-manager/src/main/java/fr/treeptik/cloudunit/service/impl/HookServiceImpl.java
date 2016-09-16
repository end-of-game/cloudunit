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

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import fr.treeptik.cloudunit.enums.RemoteExecAction;
import fr.treeptik.cloudunit.service.DockerService;
import fr.treeptik.cloudunit.service.HookService;

@Service
public class HookServiceImpl implements HookService {

    private Logger logger = LoggerFactory.getLogger(HookServiceImpl.class);

    @Inject
    private DockerService dockerService;

    @Override
    public void call(String containerName, RemoteExecAction action) {
        logger.info("Calling " + action.toString() + " Hook...");
        String response = dockerService.execCommand(containerName, action.getCommand());
        logger.info(action.toString() + " answers \"" + response + "\"");

    }

}
