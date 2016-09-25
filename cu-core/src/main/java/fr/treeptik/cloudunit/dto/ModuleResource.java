package fr.treeptik.cloudunit.dto;

import fr.treeptik.cloudunit.exception.CheckException;

public class ModuleResource {
	private Boolean publishPort;
	private String port;
	
	/**
	 * @deprecated only to be used by Jackson deserialization.
	 */
	@Deprecated
	protected ModuleResource() {}
	
	private ModuleResource(Builder builder) {
		this.publishPort = builder.publishPort;
		this.port = builder.port;
	}

	public static class Builder {

		private Boolean publishPort;

		private String port;

		public Builder withPublishPort(Boolean publishPort) {
			this.publishPort = publishPort;
			return this;
		}

		public Builder withPort(String port) {
			this.port = port;
			return this;
		}

		public ModuleResource build() {
			return new ModuleResource(this);
		}
	}

	public Boolean getPublishPort() {
		return publishPort;
	}

	public String getPort() {
		return port;
	}

	public void validatePublishPort() throws CheckException {

	}

    public static Builder of() {
        return new Builder();
    }
}
