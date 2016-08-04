package fr.treeptik.cloudunit.docker.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * Created by guillaume on 21/10/15.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class State implements Serializable {

	private static final long serialVersionUID = 1L;

	@JsonProperty("Error")
	private String error;

	@JsonProperty("ExitCode")
	private Long exitCode;

	@JsonProperty("FinishedAt")
	private String finishedAt;

	@JsonProperty("OOMKilled")
	private Boolean oOMKilled;

	@JsonProperty("Paused")
	private Boolean paused;

	@JsonProperty("Pid")
	private Long pid;

	@JsonProperty("Restarting")
	private Boolean restarting;

	@JsonProperty("Running")
	private Boolean running;

	@JsonProperty("StartedAt")
	private String startedAt;

	@JsonProperty("Dead")
	private Boolean dead;

	@JsonProperty("Status")
	private String status;

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public Long getExitCode() {
		return exitCode;
	}

	public void setExitCode(Long exitCode) {
		this.exitCode = exitCode;
	}

	public String getFinishedAt() {
		return finishedAt;
	}

	public void setFinishedAt(String finishedAt) {
		this.finishedAt = finishedAt;
	}

	public Boolean getoOMKilled() {
		return oOMKilled;
	}

	public void setoOMKilled(Boolean oOMKilled) {
		this.oOMKilled = oOMKilled;
	}

	public Boolean getPaused() {
		return paused;
	}

	public void setPaused(Boolean paused) {
		this.paused = paused;
	}

	public Long getPid() {
		return pid;
	}

	public void setPid(Long pid) {
		this.pid = pid;
	}

	public Boolean getRestarting() {
		return restarting;
	}

	public void setRestarting(Boolean restarting) {
		this.restarting = restarting;
	}

	public Boolean getRunning() {
		return running;
	}

	public void setRunning(Boolean running) {
		this.running = running;
	}

	public String getStartedAt() {
		return startedAt;
	}

	public void setStartedAt(String startedAt) {
		this.startedAt = startedAt;
	}

	public Boolean getDead() {
		return dead;
	}

	public void setDead(Boolean dead) {
		this.dead = dead;
	}
}