package fr.treeptik.cloudunitmonitor.model.action;

import java.io.Serializable;

import fr.treeptik.cloudunitmonitor.model.Server;

public abstract class ServerAction implements Serializable {

	private static final long serialVersionUID = 1L;

	protected Server parent;

	public ServerAction(Server parent) {
		this.parent = parent;
	}

	public abstract String getServerPort();

	public abstract String getServerManagerPath();

	public abstract String getServerManagerPort();

}
