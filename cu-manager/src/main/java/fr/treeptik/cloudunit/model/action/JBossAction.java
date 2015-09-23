package fr.treeptik.cloudunit.model.action;

import fr.treeptik.cloudunit.model.Server;
import fr.treeptik.cloudunit.model.Snapshot;

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
		return "cloudunit/appconf/standalone/log";
	}
}
