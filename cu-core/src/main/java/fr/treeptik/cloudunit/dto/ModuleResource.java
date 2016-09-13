package fr.treeptik.cloudunit.dto;

import fr.treeptik.cloudunit.exception.CheckException;

public class ModuleResource {
	private Boolean publishPort;
	
	/**
	 * @deprecated only to be used by Jackson deserialization.
	 */
	@Deprecated
	protected ModuleResource() {}
	
	private ModuleResource(Builder builder) {
		this.publishPort = builder.publishPort;
	}
	
	public static class Builder {
		private Boolean publishPort;
		
		public Builder withPublishPort(Boolean publishPort) {
			this.publishPort = publishPort;
			return this;
		}
		
		public ModuleResource build() {
			return new ModuleResource(this);
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
