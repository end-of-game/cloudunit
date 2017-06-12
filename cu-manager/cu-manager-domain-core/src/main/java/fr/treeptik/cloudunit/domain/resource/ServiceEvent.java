package fr.treeptik.cloudunit.domain.resource;

public class ServiceEvent  {
	public enum Type {
		CREATED, DELETED, STARTED, STOPPED, DEPLOYED;
	}

	private Type type;

	private ServiceResource service;

	public ServiceEvent() {
	}

	public ServiceEvent(Type type, ServiceResource service) {
		this.type = type;
		this.service = service;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public ServiceResource getService() {
		return service;
	}

	public void setService(ServiceResource service) {
		this.service = service;
	}

}
