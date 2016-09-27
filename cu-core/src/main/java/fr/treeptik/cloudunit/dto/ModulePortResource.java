package fr.treeptik.cloudunit.dto;

import fr.treeptik.cloudunit.exception.CheckException;

public class ModulePortResource {
	private Boolean publishPort;
	
	/**
	 * @deprecated only to be used by Jackson deserialization.
	 */
	@Deprecated
	protected ModulePortResource() {}
	
	private ModulePortResource(Builder builder) {
		this.publishPort = builder.publishPort;
	}

	public static class Builder {

		private Boolean publishPort;

		public Builder withPublishPort(Boolean publishPort) {
			this.publishPort = publishPort;
			return this;
		}

	

		public ModulePortResource build() {
			return new ModulePortResource(this);
		}
	}

	public Boolean getPublishPort() {
		return publishPort;
	}

	

	public void validatePublishPort() throws CheckException {

	}

    public static Builder of() {
        return new Builder();
    }
}
