package fr.treeptik.cloudunit.docker.builders;

import fr.treeptik.cloudunit.docker.model.HostConfig;

import java.util.List;
import java.util.Map;

/**
 * Created by guillaume on 21/10/15.
 */
public class HostConfigBuilder {
    private Boolean privileged;
    private Boolean publishAllPorts;
    private List<String> links;
    private List<String> binds;
    private Map<String, List<Map<String, String>>> portBindings;
    private List<String> volumesFrom;
    private Long blkioWeight;
    private String capAdd;
    private String capDrop;
    private String containerIDFile;
    private String cpusetCpus;
    private String cpusetMems;
    private Long cpuShares;
    private Long cpuPeriod;
    private List<String> devices;
    private List<String> dns;
    private List<String> dnsSearch;
    private List<String> dnsOptions;
    private String extraHosts;
    private String ipcMode;
    private List<String> lxcConf;
    private Long memory;
    private Long memorySwap;
    private Boolean oomKillDisable;
    private String networkMode;
    private Boolean readonlyRootfs;
    private String securityOpt;
    private List<Object> ulimits;

    private HostConfigBuilder() {
    }

    public static HostConfigBuilder aHostConfig() {
        return new HostConfigBuilder();
    }

    public HostConfigBuilder withPrivileged(Boolean privileged) {
        this.privileged = privileged;
        return this;
    }

    public HostConfigBuilder withPublishAllPorts(Boolean publishAllPorts) {
        this.publishAllPorts = publishAllPorts;
        return this;
    }

    public HostConfigBuilder withLinks(List<String> links) {
        this.links = links;
        return this;
    }

    public HostConfigBuilder withBinds(List<String> binds) {
        this.binds = binds;
        return this;
    }

    public HostConfigBuilder withPortBindings(Map<String, List<Map<String, String>>> portBindings) {
        this.portBindings = portBindings;
        return this;
    }

    public HostConfigBuilder withVolumesFrom(List<String> volumesFrom) {
        this.volumesFrom = volumesFrom;
        return this;
    }

    public HostConfigBuilder withBlkioWeight(Long blkioWeight) {
        this.blkioWeight = blkioWeight;
        return this;
    }

    public HostConfigBuilder withCapAdd(String capAdd) {
        this.capAdd = capAdd;
        return this;
    }

    public HostConfigBuilder withCapDrop(String capDrop) {
        this.capDrop = capDrop;
        return this;
    }

    public HostConfigBuilder withContainerIDFile(String containerIDFile) {
        this.containerIDFile = containerIDFile;
        return this;
    }

    public HostConfigBuilder withCpusetCpus(String cpusetCpus) {
        this.cpusetCpus = cpusetCpus;
        return this;
    }

    public HostConfigBuilder withCpusetMems(String cpusetMems) {
        this.cpusetMems = cpusetMems;
        return this;
    }

    public HostConfigBuilder withCpuShares(Long cpuShares) {
        this.cpuShares = cpuShares;
        return this;
    }

    public HostConfigBuilder withCpuPeriod(Long cpuPeriod) {
        this.cpuPeriod = cpuPeriod;
        return this;
    }

    public HostConfigBuilder withDevices(List<String> devices) {
        this.devices = devices;
        return this;
    }

    public HostConfigBuilder withDns(List<String> dns) {
        this.dns = dns;
        return this;
    }

    public HostConfigBuilder withDnsSearch(List<String> dnsSearch) {
        this.dnsSearch = dnsSearch;
        return this;
    }

    public HostConfigBuilder withExtraHosts(String extraHosts) {
        this.extraHosts = extraHosts;
        return this;
    }

    public HostConfigBuilder withIpcMode(String ipcMode) {
        this.ipcMode = ipcMode;
        return this;
    }

    public HostConfigBuilder withLxcConf(List<String> lxcConf) {
        this.lxcConf = lxcConf;
        return this;
    }

    public HostConfigBuilder withMemory(Long memory) {
        this.memory = memory;
        return this;
    }

    public HostConfigBuilder withMemorySwap(Long memorySwap) {
        this.memorySwap = memorySwap;
        return this;
    }

    public HostConfigBuilder withOomKillDisable(Boolean oomKillDisable) {
        this.oomKillDisable = oomKillDisable;
        return this;
    }

    public HostConfigBuilder withNetworkMode(String networkMode) {
        this.networkMode = networkMode;
        return this;
    }

    public HostConfigBuilder withReadonlyRootfs(Boolean readonlyRootfs) {
        this.readonlyRootfs = readonlyRootfs;
        return this;
    }

    public HostConfigBuilder withSecurityOpt(String securityOpt) {
        this.securityOpt = securityOpt;
        return this;
    }

    public HostConfigBuilder withUlimits(List<Object> ulimits) {
        this.ulimits = ulimits;
        return this;
    }

    public HostConfigBuilder but() {
        return aHostConfig().withPrivileged(privileged).withPublishAllPorts(publishAllPorts).withLinks(links)
                .withBinds(binds).withPortBindings(portBindings).withVolumesFrom(volumesFrom)
                .withBlkioWeight(blkioWeight).withCapAdd(capAdd).withCapDrop(capDrop)
                .withContainerIDFile(containerIDFile).withCpusetCpus(cpusetCpus).withCpusetMems(cpusetMems)
                .withCpuShares(cpuShares).withCpuPeriod(cpuPeriod).withDevices(devices).withDns(dns)
                .withDnsSearch(dnsSearch).withExtraHosts(extraHosts).withIpcMode(ipcMode).withLxcConf(lxcConf)
                .withMemory(memory).withMemorySwap(memorySwap).withOomKillDisable(oomKillDisable)
                .withNetworkMode(networkMode).withReadonlyRootfs(readonlyRootfs).withSecurityOpt(securityOpt)
                .withUlimits(ulimits);
    }

    public HostConfig build() {
        HostConfig hostConfig = new HostConfig();
        hostConfig.setPrivileged(privileged);
        hostConfig.setPublishAllPorts(publishAllPorts);
        hostConfig.setLinks(links);
        hostConfig.setBinds(binds);
        hostConfig.setPortBindings(portBindings);
        hostConfig.setVolumesFrom(volumesFrom);
        hostConfig.setBlkioWeight(blkioWeight);
        hostConfig.setCapAdd(capAdd);
        hostConfig.setCapDrop(capDrop);
        hostConfig.setContainerIDFile(containerIDFile);
        hostConfig.setCpusetCpus(cpusetCpus);
        hostConfig.setCpusetMems(cpusetMems);
        hostConfig.setCpuShares(cpuShares);
        hostConfig.setCpuPeriod(cpuPeriod);
        hostConfig.setDevices(devices);
        hostConfig.setDns(dns);
        hostConfig.setDnsSearch(dnsSearch);
        hostConfig.setDnsOptions(dnsOptions);
        hostConfig.setExtraHosts(extraHosts);
        hostConfig.setIpcMode(ipcMode);
        hostConfig.setLxcConf(lxcConf);
        hostConfig.setMemory(memory);
        hostConfig.setMemorySwap(memorySwap);
        hostConfig.setOomKillDisable(oomKillDisable);
        hostConfig.setNetworkMode(networkMode);
        hostConfig.setReadonlyRootfs(readonlyRootfs);
        hostConfig.setSecurityOpt(securityOpt);
        hostConfig.setUlimits(ulimits);
        return hostConfig;
    }
}
