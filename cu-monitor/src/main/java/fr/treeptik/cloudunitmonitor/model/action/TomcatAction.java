package fr.treeptik.cloudunitmonitor.model.action;

import fr.treeptik.cloudunitmonitor.model.Server;

public class TomcatAction extends ServerAction {

	private static final long serialVersionUID = 1L;

	public TomcatAction(Server parent) {
		super(parent);
	}

	@Override
	public String getServerManagerPath() {
		return "/manager";
	}

	@Override
	public String getServerManagerPort() {
		return "8080";
	}

	@Override
	public String getServerPort() {
		return "8080";
	}
}
