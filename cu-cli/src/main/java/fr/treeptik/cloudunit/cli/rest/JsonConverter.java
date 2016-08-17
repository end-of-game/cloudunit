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

package fr.treeptik.cloudunit.cli.rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.treeptik.cloudunit.dto.ContainerUnit;
import fr.treeptik.cloudunit.dto.FileUnit;
import fr.treeptik.cloudunit.dto.HttpErrorServer;
import fr.treeptik.cloudunit.dto.LogUnit;
import fr.treeptik.cloudunit.model.Application;
import fr.treeptik.cloudunit.model.Image;
import fr.treeptik.cloudunit.model.Message;
import fr.treeptik.cloudunit.model.Module;
import fr.treeptik.cloudunit.model.Server;
import fr.treeptik.cloudunit.model.Snapshot;
import fr.treeptik.cloudunit.model.User;

public class JsonConverter {

	public static Application getApplication(String response) {
		Application application = new Application();
		ObjectMapper mapper = new ObjectMapper();
		try {
			application = mapper.readValue(response, Application.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return application;
	}

	public static Image getImage(String response) {
		Image image = new Image();
		ObjectMapper mapper = new ObjectMapper();
		try {
			image = mapper.readValue(response, Image.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return image;
	}

	public static HttpErrorServer getError(String response) {
		HttpErrorServer error = new HttpErrorServer();
		ObjectMapper mapper = new ObjectMapper();
		try {
			error = mapper.readValue(response, HttpErrorServer.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return error;
	}

	public static User getUser(String response) {
		User user = new User();
		ObjectMapper mapper = new ObjectMapper();
		try {
			user = mapper.readValue(response, User.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return user;
	}

	public static String getCloudUnitInstance(String response) {
		String cloudunitInstance = null;
		ObjectMapper mapper = new ObjectMapper();
		try {
			Map<String, String> map = mapper.readValue(response, new TypeReference<Map<String, String>>() {
			});
			cloudunitInstance = map.get("cuInstanceName");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return cloudunitInstance;
	}

	public static List<Application> getApplications(String response) {
		List<Application> applications = new ArrayList<>();
		ObjectMapper mapper = new ObjectMapper();
		try {
			applications = mapper.readValue(response, new TypeReference<List<Application>>() {
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
		return applications;
	}

	public static List<Snapshot> getSnapshot(String response) {
		List<Snapshot> snapshots = new ArrayList<>();
		ObjectMapper mapper = new ObjectMapper();
		try {
			snapshots = mapper.readValue(response, new TypeReference<List<Snapshot>>() {
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
		return snapshots;
	}

	public static List<LogUnit> getLogUnit(String response) {
		List<LogUnit> logUnits = new ArrayList<LogUnit>();
		ObjectMapper mapper = new ObjectMapper();
		try {
			logUnits = mapper.readValue(response, new TypeReference<List<LogUnit>>() {
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
		return logUnits;
	}

	public static List<User> getUsers(String response) {
		List<User> users = new ArrayList<>();
		ObjectMapper mapper = new ObjectMapper();
		try {
			users = mapper.readValue(response, new TypeReference<List<User>>() {
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
		return users;
	}

	public static List<Image> getImages(String response) {
		List<Image> images = new ArrayList<>();
		ObjectMapper mapper = new ObjectMapper();
		try {
			images = mapper.readValue(response, new TypeReference<List<Image>>() {
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
		return images;
	}

	public static List<String> getTags(String response) {
		List<String> tags = new ArrayList<>();
		ObjectMapper mapper = new ObjectMapper();
		try {
			tags = mapper.readValue(response, new TypeReference<List<String>>() {
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
		return tags;
	}

	public static Server getServer(String response) {
		Server server = new Server();
		ObjectMapper mapper = new ObjectMapper();
		try {
			server = mapper.readValue(response, Server.class);
		} catch (IOException e) {

			e.printStackTrace();
		}
		return server;
	}

	public static Module getModule(String response) {
		Module module = new Module();
		ObjectMapper mapper = new ObjectMapper();
		try {
			module = mapper.readValue(response, Module.class);
		} catch (IOException e) {

			e.printStackTrace();
		}
		return module;
	}

	public static List<Message> getMessage(String response) {
		ObjectMapper mapper = new ObjectMapper();
		List<Message> messages = null;
		try {
			messages = mapper.readValue(response, new TypeReference<List<Message>>() {
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
		return messages;
	}

	public static List<ContainerUnit> getContainerUnits(String response) {
		ObjectMapper mapper = new ObjectMapper();
		List<ContainerUnit> containerUnits = null;
		try {
			containerUnits = mapper.readValue(response, new TypeReference<List<ContainerUnit>>() {
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
		return containerUnits;
	}

	public static List<String> getAliases(String response) {
		List<String> tags = new ArrayList<>();
		ObjectMapper mapper = new ObjectMapper();
		try {
			tags = mapper.readValue(response, new TypeReference<List<String>>() {
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
		return tags;
	}

	public static List<FileUnit> getFileUnits(String response) {
		List<FileUnit> fileUnits = new ArrayList<FileUnit>();
		ObjectMapper mapper = new ObjectMapper();
		try {
			fileUnits = mapper.readValue(response, new TypeReference<List<FileUnit>>() {
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
		return fileUnits;
	}
}
