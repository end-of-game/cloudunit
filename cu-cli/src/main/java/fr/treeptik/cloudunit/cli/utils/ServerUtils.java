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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.treeptik.cloudunit.cli.CloudUnitCliException;
import fr.treeptik.cloudunit.cli.exception.ManagerResponseException;
import fr.treeptik.cloudunit.cli.processor.InjectLogger;
import fr.treeptik.cloudunit.cli.rest.RestUtils;
import fr.treeptik.cloudunit.model.Application;

@Component
public class ServerUtils {

	@InjectLogger
	private Logger log;

	@Autowired
	private AuthenticationUtils authenticationUtils;

	@Autowired
	private ApplicationUtils applicationUtils;

	@Autowired
	private RestUtils restUtils;

	private List<String> availableJavaVersion = Arrays.asList(new String[] { "java7", "java8", "java9" });

	private List<String> availableMemoryValues = Arrays.asList("512", "1024", "2048", "3072");

	/**
	 * @param memory
	 * @return
	 */
	public String changeMemory(String memory) {
	    applicationUtils.checkApplicationSelected();

		if (!availableMemoryValues.contains(memory)) {
			throw new CloudUnitCliException("The memory value you have put is not authorized (512, 1024, 2048, 3072)");
		}

		Map<String, String> parameters = new HashMap<>();
		parameters.put("applicationName", applicationUtils.getCurrentApplication().getName());
		parameters.put("jvmMemory", memory);
		parameters.put("jvmRelease", applicationUtils.getCurrentApplication().getJvmRelease());
		parameters.put("jvmOptions", applicationUtils.getCurrentApplication().getServer().getJvmOptions().toString());
		try {
			restUtils.sendPutCommand(authenticationUtils.finalHost + "/server/configuration/jvm",
					authenticationUtils.getMap(), parameters).get("body");
		} catch (ManagerResponseException e) {
		    throw new CloudUnitCliException("Couldn't change memory", e);
		}

		return "Change memory on " + applicationUtils.getCurrentApplication().getName() + " successful";
	}

	/**
	 * Add an option for JVM
	 *
	 * @param opts
	 * @return
	 */
	public String addOpts(String opts) {
        applicationUtils.checkApplicationSelected();

		Map<String, String> parameters = new HashMap<>();
		parameters.put("applicationName", applicationUtils.getCurrentApplication().getName());
		parameters.put("jvmOptions", opts);
		parameters.put("jvmRelease", applicationUtils.getCurrentApplication().getJvmRelease());
		parameters.put("jvmMemory", applicationUtils.getCurrentApplication().getServer().getJvmMemory().toString());
		try {
			restUtils.sendPutCommand(authenticationUtils.finalHost + "/server/configuration/jvm",
					authenticationUtils.getMap(), parameters).get("body");
		} catch (ManagerResponseException e) {
			throw new CloudUnitCliException("Couldn't add JVM option", e);
		}

		return "Add java options to " + applicationUtils.getCurrentApplication().getName() + " application successfully";
	}

	/**
	 * Change JVM Release
	 *
	 * @param applicationName
	 * @param jvmRelease
	 * @return
	 */
	public String changeJavaVersion(String applicationName, String jvmRelease) {
	    Application application = applicationUtils.getSpecificOrCurrentApplication(applicationName);
	    
		if (!availableJavaVersion.contains(jvmRelease)) {
			throw new CloudUnitCliException("The specified java version is not available");
		}

		Map<String, String> parameters = new HashMap<>();
		parameters.put("applicationName", application.getName());
		parameters.put("jvmRelease", jvmRelease);
		parameters.put("jvmMemory", application.getServer().getJvmMemory().toString());
		parameters.put("jvmOptions", application.getServer().getJvmOptions().toString());
		try {
			restUtils.sendPutCommand(authenticationUtils.finalHost + "/server/configuration/jvm",
					authenticationUtils.getMap(), parameters).get("body");
		} catch (ManagerResponseException e) {
		    throw new CloudUnitCliException("Couldn't change Java version", e);
		}

		return "Your java version has been successfully changed";
	}

	public String openPort(String applicationName, String portToOpen, String portNature) {
	    Application application = applicationUtils.getSpecificOrCurrentApplication(applicationName);

		Map<String, String> parameters = new HashMap<>();
		parameters.put("applicationName", application.getName());
		parameters.put("portToOpen", portToOpen);
		parameters.put("portNature", portNature);

		try {
			restUtils.sendPostCommand(authenticationUtils.finalHost + "/application/ports",
					authenticationUtils.getMap(), parameters).get("body");
		} catch (ManagerResponseException e) {
		}

		return "The port " + portToOpen + " was been successfully opened on "+ application.getName();
	}

	/**
	 * @param applicationName
	 * @param portToOpen
	 * @return
	 */
	public String removePort(String applicationName, String portToOpen) {
        Application application = applicationUtils.getSpecificOrCurrentApplication(applicationName);

		try {
			restUtils.sendDeleteCommand(
					authenticationUtils.finalHost + "/application/" + application.getName() + "/ports/" + portToOpen,
					authenticationUtils.getMap()).get("body");
		} catch (ManagerResponseException e) {
		    throw new CloudUnitCliException("Couldn't remove port", e);
		}

		return "The port " + portToOpen + " was been successfully closed on " + application.getName();
	}

	public String mountVolume(String name, String path, Boolean mode, String containerName, String applicationName) {
	    Application application = applicationUtils.getSpecificOrCurrentApplication(applicationName);
	    
		if (containerName == null)
		{
			containerName = application.getServer().getName();
		}

		try {
			Map<String, String> parameters = new HashMap<>();
			parameters.put("containerName", containerName);
			parameters.put("path", path);

			if(mode.equals(true)) parameters.put("mode", "ro");
			else parameters.put("mode", "rw");

			parameters.put("volumeName", name);
			parameters.put("applicationName", application.getName());
			restUtils.sendPutCommand(authenticationUtils.finalHost + "/server/volume", authenticationUtils.getMap(), parameters);
		} catch (ManagerResponseException e) {
		    throw new CloudUnitCliException("Couldn't mount volume", e);
		}

		return "This volume has successful been mounted";
	}

	public String unmountVolume(String name, String containerName) {
		try {
			restUtils.sendDeleteCommand(authenticationUtils.finalHost + "/server/volume/" + name + "/container/" +
					containerName, authenticationUtils.getMap());
		} catch (ManagerResponseException e) {
		    throw new CloudUnitCliException("Couldn't unmount volume", e);
		}

		return "This volume has successful been unmounted";
	}

}
