/*
 * LICENCE : CloudUnit is available under the Gnu Public License GPL V3 : https://www.gnu.org/licenses/gpl.txt
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

package fr.treeptik.cloudunit.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fr.treeptik.cloudunit.model.action.ServerAction;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.PostLoad;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Entity
public class Server extends Container implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long jvmMemory;

	private String jvmOptions;

	private String jvmRelease;

	private String managerLocation;

	@JsonIgnore
	@ElementCollection
	private List<String> links;

	@Transient
	@JsonIgnore
	private ServerAction serverAction;

	@PostLoad
	public void initServerActionFromJPA() {
		ServerFactory.updateServer(this);
	}

	public Server(Integer id, Date startDate, String name, String containerID,
			Long memorySize, String containerIP, String dockerState,
			Status status, Image image, Map<String, String> listPorts) {
		super();
		this.id = id;
		this.startDate = startDate;
		this.name = name;
		this.containerID = containerID;
		this.memorySize = memorySize;
		this.containerIP = containerIP;
		this.dockerState = dockerState;
		this.status = status;
		this.image = image;
	}

	public Server() {
	}

	public Long getJvmMemory() {
		return jvmMemory;
	}

	public void setJvmMemory(Long jvmMemory) {
		this.jvmMemory = jvmMemory;
	}

	public String getJvmOptions() {
		return jvmOptions;
	}

	public void setJvmOptions(String opts) {
		this.jvmOptions = opts;
	}

	public String getManagerLocation() {
		return managerLocation;
	}

	public void setManagerLocation(String managerLocation) {
		this.managerLocation = managerLocation;
	}

	public ServerAction getServerAction() {
		return serverAction;
	}

	public void setServerAction(ServerAction serverAction) {
		this.serverAction = serverAction;
	}

	public String getJvmRelease() {
		return jvmRelease;
	}

	public void setJvmRelease(String jvmRelease) {
		this.jvmRelease = jvmRelease;
	}

	@Override
	public String toString() {
		return "Server [id=" + id + ", startDate=" + startDate + ", name="
				+ name + ", cloudId=" + containerID + ", memorySize="
				+ memorySize + ", containerIP=" + containerIP
				+ ", dockerState=" + dockerState + ", image=" + image
				+ ", status=" + status + "]";
	}

}
