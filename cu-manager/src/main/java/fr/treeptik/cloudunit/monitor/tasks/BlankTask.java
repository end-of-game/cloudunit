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

package fr.treeptik.cloudunit.monitor.tasks;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import fr.treeptik.cloudunit.monitor.Task;

/**
 * Created by nicolas on 27/11/2015.
 */
@Component
public class BlankTask implements Task {

	@Override
	public void run() {
		isRunning(true);
	}

	// Two minutes
	@Scheduled(fixedDelay = 120000)
	private void monitor() {
		isRunning(false);
	}

	/**
	 * To evaluate if service is runnning or not If needed, we stop the jvm or
	 * send an email
	 *
	 * @param exit
	 */
	private void isRunning(boolean exit) {
	}

}
