package fr.treeptik.cloudunit.docker.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

/**
 * Created by guillaume on 22/10/15.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExecBody implements Serializable {

	private static final long serialVersionUID = 1L;

	@JsonProperty("AttachStdin")
	private Boolean attachStdin;

	@JsonProperty("AttachStdout")
	private Boolean attachStdout;

	@JsonProperty("AttachStderr")
	private Boolean attachStderr;

	@JsonProperty("Tty")
	private Boolean tty;

	@JsonProperty("Cmd")
	private List<String> cmd;

	@JsonProperty("Id")
	private String Id;

	@JsonProperty("Warning")
	private List<String> warnings;

	public Boolean getAttachStdin() {
		return attachStdin;
	}

	public void setAttachStdin(Boolean attachStdin) {
		this.attachStdin = attachStdin;
	}

	public Boolean getAttachStdout() {
		return attachStdout;
	}

	public void setAttachStdout(Boolean attachStdout) {
		this.attachStdout = attachStdout;
	}

	public Boolean getAttachStderr() {
		return attachStderr;
	}

	public void setAttachStderr(Boolean attachStderr) {
		this.attachStderr = attachStderr;
	}

	public Boolean getTty() {
		return tty;
	}

	public void setTty(Boolean tty) {
		this.tty = tty;
	}

	public List<String> getCmd() {
		return cmd;
	}

	public void setCmd(List<String> cmd) {
		this.cmd = cmd;
	}

	public String getId() {
		return Id;
	}

	public void setId(String id) {
		Id = id;
	}

	public List<String> getWarnings() {
		return warnings;
	}

	public void setWarnings(List<String> warnings) {
		this.warnings = warnings;
	}
}
