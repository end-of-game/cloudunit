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

package fr.treeptik.cloudunit.model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import fr.treeptik.cloudunit.model.action.ServerAction;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;
import javax.persistence.PostLoad;
import javax.persistence.Transient;

@Entity
public class Server extends Container implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long jvmMemory;

	@Column(columnDefinition = "TEXT")
	private String jvmOptions;

	private String jvmRelease;

	private String managerLocation;

	@JsonIgnore
	@OneToOne(mappedBy = "server", fetch = FetchType.LAZY)
	private Application application;

	@JsonIgnore
	@ElementCollection
	private List<String> links;

	@Transient
	@JsonIgnore
	private ServerAction serverAction;

	public Server(Integer id, Date startDate, String name, String containerID, Long memorySize, String containerIP,
			Status status, Image image, Map<String, String> listPorts) {
		super();
		this.id = id;
		this.startDate = startDate;
		this.name = name;
		this.containerID = containerID;
		this.memorySize = memorySize;
		this.containerIP = containerIP;
		this.status = status;
		this.image = image;
	}

	public Server() {
	}

	@PostLoad
	public void initServerActionFromJPA() {
		ServerFactory.updateServer(this);
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
		return "Server [id=" + id + ", startDate=" + startDate + ", name=" + name + ", cloudId=" + containerID
				+ ", memorySize=" + memorySize + ", containerIP=" + containerIP + ", image=" + image + ", status="
				+ status + "]";
	}

	public Application getApplication() {
		return application;
	}

	public void setApplication(Application application) {
		this.application = application;
	}

}
