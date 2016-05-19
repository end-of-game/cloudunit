package fr.treeptik.cloudunitmonitor.model.action;

import fr.treeptik.cloudunitmonitor.model.Server;

public class JBossAction5 extends ServerAction {

	public JBossAction5(Server parent) {
		super(parent);
	}

	private static final long serialVersionUID = 1L;

	@Override
	public String getServerManagerPath() {
		return "/admin-console";
	}

	@Override
	public String getServerPort() {
		return "8080";
	}

	@Override
	public String getServerManagerPort() {
		return "8080";
	}

}
