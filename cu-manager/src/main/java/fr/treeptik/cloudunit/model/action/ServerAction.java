package fr.treeptik.cloudunit.model.action;

import fr.treeptik.cloudunit.model.Server;
import fr.treeptik.cloudunit.model.Snapshot;

import java.io.Serializable;

public abstract class ServerAction implements Serializable {

	private static final long serialVersionUID = 1L;

	protected Server parent;

	public ServerAction(Server parent) {
		this.parent = parent;
	}

	public abstract String getServerManagerPath();

	public abstract String getServerManagerPort();

	public abstract String getServerPort();

	public abstract Snapshot cloneProperties(Snapshot snapshot);

	public abstract String cleanCommand();

	public abstract String getLogLocation();

}
