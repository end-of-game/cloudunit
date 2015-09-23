package fr.treeptik.cloudunit.model.action;

import fr.treeptik.cloudunit.model.Server;
import fr.treeptik.cloudunit.model.Snapshot;

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

	@Override
	public Snapshot cloneProperties(Snapshot snapshot) {
		snapshot.setType(parent.getImage().getName());
		snapshot.setJvmRelease(parent.getJvmRelease());
		snapshot.setJvmOptions(parent.getJvmOptions());
		snapshot.setJvmMemory(parent.getJvmMemory());
		return snapshot;
	}

	@Override
	public String cleanCommand() {
		return null;
	}

	@Override
	public String getLogLocation() {
		return "cloudunit/appconf/server/default/log";
	}

}
