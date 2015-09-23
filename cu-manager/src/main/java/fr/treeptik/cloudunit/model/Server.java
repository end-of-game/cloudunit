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
