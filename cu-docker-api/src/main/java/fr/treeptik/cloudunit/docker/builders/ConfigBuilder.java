package fr.treeptik.cloudunit.docker.builders;

import fr.treeptik.cloudunit.docker.model.Config;
import fr.treeptik.cloudunit.docker.model.HostConfig;

import java.util.List;
import java.util.Map;

/**
 * Created by guillaume on 21/10/15.
 */
public class ConfigBuilder {
    private Boolean attachStdin;
    private Boolean attachStdout;
    private Boolean attachStderr;
    private Long memory;
    private Long memorySwap;
    private String image;
    private List<String> cmd;
    private Map<String, Object> exposedPorts;
    private HostConfig hostConfig;
    private Long cpuShares;
    private String cpuset;
    private String domainname;
    private List<String> entrypoint;
    private List<String> env;
    private String hostname;
    private Map<String, String> labels;
    private String macAddress;
    private Boolean networkDisabled;
    private String onBuild;
    private Boolean openStdin;
    private String portSpecs;
    private Boolean stdinOnce;
    private Boolean tty;
    private String user;
    private Map<String, Map<String, String>> volumes;

    private String workingDir;

    private ConfigBuilder() {
    }

    public static ConfigBuilder aConfig() {
        return new ConfigBuilder();
    }

    public ConfigBuilder withAttachStdin(Boolean attachStdin) {
        this.attachStdin = attachStdin;
        return this;
    }

    public ConfigBuilder withAttachStdout(Boolean attachStdout) {
        this.attachStdout = attachStdout;
        return this;
    }

    public ConfigBuilder withAttachStderr(Boolean attachStderr) {
        this.attachStderr = attachStderr;
        return this;
    }

    public ConfigBuilder withMemory(Long memory) {
        this.memory = memory;
        return this;
    }

    public ConfigBuilder withMemorySwap(Long memorySwap) {
        this.memorySwap = memorySwap;
        return this;
    }

    public ConfigBuilder withImage(String image) {
        this.image = image;
        return this;
    }

    public ConfigBuilder withCmd(List<String> cmd) {
        this.cmd = cmd;
        return this;
    }

    public ConfigBuilder withExposedPorts(Map<String, Object> exposedPorts) {
        this.exposedPorts = exposedPorts;
        return this;
    }

    public ConfigBuilder withHostConfig(HostConfig hostConfig) {
        this.hostConfig = hostConfig;
        return this;
    }

    public ConfigBuilder withCpuShares(Long cpuShares) {
        this.cpuShares = cpuShares;
        return this;
    }

    public ConfigBuilder withCpuset(String cpuset) {
        this.cpuset = cpuset;
        return this;
    }

    public ConfigBuilder withDomainname(String domainname) {
        this.domainname = domainname;
        return this;
    }

    public ConfigBuilder withEntrypoint(List<String> entrypoint) {
        this.entrypoint = entrypoint;
        return this;
    }

    public ConfigBuilder withEnv(List<String> env) {
        this.env = env;
        return this;
    }

    public ConfigBuilder withHostname(String hostname) {
        this.hostname = hostname;
        return this;
    }

    public ConfigBuilder withLabels(Map<String, String> labels) {
        this.labels = labels;
        return this;
    }

    public ConfigBuilder withMacAddress(String macAddress) {
        this.macAddress = macAddress;
        return this;
    }

    public ConfigBuilder withNetworkDisabled(Boolean networkDisabled) {
        this.networkDisabled = networkDisabled;
        return this;
    }

    public ConfigBuilder withOnBuild(String onBuild) {
        this.onBuild = onBuild;
        return this;
    }

    public ConfigBuilder withOpenStdin(Boolean openStdin) {
        this.openStdin = openStdin;
        return this;
    }

    public ConfigBuilder withPortSpecs(String portSpecs) {
        this.portSpecs = portSpecs;
        return this;
    }

    public ConfigBuilder withStdinOnce(Boolean stdinOnce) {
        this.stdinOnce = stdinOnce;
        return this;
    }

    public ConfigBuilder withTty(Boolean tty) {
        this.tty = tty;
        return this;
    }

    public ConfigBuilder withUser(String user) {
        this.user = user;
        return this;
    }

    public ConfigBuilder withVolumes(Map<String, Map<String, String>> volumes) {
        this.volumes = volumes;
        return this;
    }

    public ConfigBuilder withWorkingDir(String workingDir) {
        this.workingDir = workingDir;
        return this;
    }

    public ConfigBuilder but() {
        return aConfig().withAttachStdin(attachStdin).withAttachStdout(attachStdout).withAttachStderr(attachStderr).withMemory(memory).withMemorySwap(memorySwap).withImage(image).withCmd(cmd).withExposedPorts(exposedPorts).withHostConfig(hostConfig).withCpuShares(cpuShares).withCpuset(cpuset).withDomainname(domainname).withEntrypoint(entrypoint).withEnv(env).withHostname(hostname).withLabels(labels).withMacAddress(macAddress).withNetworkDisabled(networkDisabled).withOnBuild(onBuild).withOpenStdin(openStdin).withPortSpecs(portSpecs).withStdinOnce(stdinOnce).withTty(tty).withUser(user).withVolumes(volumes).withWorkingDir(workingDir);
    }

    public Config build() {
        Config config = new Config();
        config.setAttachStdin(attachStdin);
        config.setAttachStdout(attachStdout);
        config.setAttachStderr(attachStderr);
        config.setMemory(memory);
        config.setMemorySwap(memorySwap);
        config.setImage(image);
        config.setCmd(cmd);
        config.setExposedPorts(exposedPorts);
        config.setHostConfig(hostConfig);
        config.setCpuShares(cpuShares);
        config.setCpuset(cpuset);
        config.setDomainname(domainname);
        config.setEntrypoint(entrypoint);
        config.setEnv(env);
        config.setHostname(hostname);
        config.setLabels(labels);
        config.setMacAddress(macAddress);
        config.setNetworkDisabled(networkDisabled);
        config.setOnBuild(onBuild);
        config.setOpenStdin(openStdin);
        config.setPortSpecs(portSpecs);
        config.setStdinOnce(stdinOnce);
        config.setTty(tty);
        config.setUser(user);
        config.setVolumes(volumes);
        config.setWorkingDir(workingDir);
        return config;
    }
}
