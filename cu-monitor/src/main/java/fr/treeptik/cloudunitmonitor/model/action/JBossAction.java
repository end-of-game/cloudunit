package fr.treeptik.cloudunitmonitor.model.action;


import fr.treeptik.cloudunitmonitor.model.Server;

public class JBossAction extends ServerAction {

	private static final long serialVersionUID = 1L;

	public JBossAction(Server parent) {
		super(parent);
	}

	@Override
	public String getServerManagerPath() {
		return "";
	}

	@Override
	public String getServerManagerPort() {
		return "9990";
	}

	@Override
	public String getServerPort() {
		return "8080";
	}

}
