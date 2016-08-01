package fr.treeptik.cloudunit.docker.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by guillaume on 21/10/15.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class HostConfig implements Serializable {

    @JsonProperty("Privileged")
    private Boolean privileged;

    @JsonProperty("PublishAllPorts")
    private Boolean publishAllPorts;

    @JsonProperty("Links")
    private List<String> links;

    @JsonProperty("Binds")
    private List<String> binds;

    @JsonProperty("PortBindings")
    private Map<String, List<Object>> portBindings;

    @JsonProperty("VolumesFrom")
    private List<String> volumesFrom;

    @JsonProperty("BlkioWeight")
    private Long blkioWeight;

    @JsonProperty("CapAdd")
    private String capAdd;

    @JsonProperty("CapDrop")
    private String capDrop;

    @JsonProperty("ContainerIDFile")
    private String containerIDFile;

    @JsonProperty("CpusetCpus")
    private String cpusetCpus;

    @JsonProperty("CpusetMems")
    private String cpusetMems;

    @JsonProperty("CpuShares")
    private Long cpuShares;

    @JsonProperty("CpuPeriod")
    private Long cpuPeriod;

    @JsonProperty("Devices")
    private List<String> devices;

    public void setDns(List<String> dns) {
        this.dns = dns;
    }

    public List<String> getDns() {
        return dns;
    }

    public Long getKernelMemory() {
        return kernelMemory;
    }

    public void setKernelMemory(Long kernelMemory) {
        this.kernelMemory = kernelMemory;
    }

    public Long getCpuQuota() {
        return cpuQuota;
    }

    public void setCpuQuota(Long cpuQuota) {
        this.cpuQuota = cpuQuota;
    }

    public Boolean getMemorySwappiness() {
        return memorySwappiness;
    }

    public void setMemorySwappiness(Boolean memorySwappiness) {
        this.memorySwappiness = memorySwappiness;
    }

    public Boolean getGroupAdd() {
        return groupAdd;
    }

    public void setGroupAdd(Boolean groupAdd) {
        this.groupAdd = groupAdd;
    }

    public String getuTSMode() {
        return uTSMode;
    }

    public void setuTSMode(String uTSMode) {
        this.uTSMode = uTSMode;
    }

    public List<Long> getConsoleSize() {
        return consoleSize;
    }

    public void setConsoleSize(List<Long> consoleSize) {
        this.consoleSize = consoleSize;
    }

    public String getVolumeDriver() {
        return volumeDriver;
    }

    public void setVolumeDriver(String volumeDriver) {
        this.volumeDriver = volumeDriver;
    }

    @JsonProperty("Dns")
    private List<String> dns;

    @JsonProperty("DnsSearch")
    private List<String> dnsSearch;

    public List<String> getDnsOptions() {
        return dnsOptions;
    }

    public void setDnsOptions(List<String> dnsOptions) {
        this.dnsOptions = dnsOptions;
    }

    @JsonProperty("DnsOptions")
    private List<String> dnsOptions;

    public void setDnsSearch(List<String> dnsSearch) {
        this.dnsSearch = dnsSearch;
    }

    @JsonProperty("ExtraHosts")
    private String extraHosts;

    @JsonProperty("IpcMode")
    private String ipcMode;

    @JsonProperty("LxcConf")
    private List<String> lxcConf;

    @JsonProperty("Memory")
    private Long memory;

    @JsonProperty("MemorySwap")
    private Long memorySwap;

    @JsonProperty("OomKillDisable")
    private Boolean oomKillDisable;

    @JsonProperty("NetworkMode")
    private String networkMode;

    @JsonProperty("ReadonlyRootfs")
    private Boolean readonlyRootfs;

    @JsonProperty("SecurityOpt")
    private String securityOpt;

    @JsonProperty("Ulimits")
    private List<Object> ulimits;

    @JsonProperty("CgroupParent")
    private String cgroupParent;

    @JsonProperty("PidMode")
    private String pidMode;

    @JsonProperty("RestartPolicy")
    private RestartPolicy restartPolicy;

    @JsonProperty("LogConfig")
    private LogConfig logConfig;

    @JsonProperty("MemoryReservation")
    private Long memoryReservation;

    @JsonProperty("KernelMemory")
    private Long kernelMemory;

    @JsonProperty("CpuQuota")
    private Long cpuQuota;

    @JsonProperty("MemorySwappiness")
    private Boolean memorySwappiness;

    @JsonProperty("GroupAdd")
    private Boolean groupAdd;

    @JsonProperty("UTSMode")
    private String uTSMode;

    @JsonProperty("ConsoleSize")
    private List<Long> consoleSize;

    @JsonProperty("VolumeDriver")
    private String volumeDriver;

    @JsonProperty("ShmSize")
    private String ShmSize;

    public String getShmSize() {
        return ShmSize;
    }

    public void setShmSize(String shmSize) {
        ShmSize = shmSize;
    }

    public String getOomScoreAdj() {
        return oomScoreAdj;
    }

    public void setOomScoreAdj(String oomScoreAdj) {
        oomScoreAdj = oomScoreAdj;
    }

    @JsonProperty("Isolation")
    private String isolation;

    @JsonProperty("OomScoreAdj")
    private String oomScoreAdj;

    public String getBlkioDeviceReadBps() {
        return BlkioDeviceReadBps;
    }

    public void setBlkioDeviceReadBps(String blkioDeviceReadBps) {
        BlkioDeviceReadBps = blkioDeviceReadBps;
    }

    public String getBlkioDeviceWriteBps() {
        return BlkioDeviceWriteBps;
    }

    public void setBlkioDeviceWriteBps(String blkioDeviceWriteBps) {
        BlkioDeviceWriteBps = blkioDeviceWriteBps;
    }

    public String getBlkioDeviceReadIOps() {
        return BlkioDeviceReadIOps;
    }

    public void setBlkioDeviceReadIOps(String blkioDeviceReadIOps) {
        BlkioDeviceReadIOps = blkioDeviceReadIOps;
    }

    public String getBlkioDeviceWriteIOps() {
        return BlkioDeviceWriteIOps;
    }

    public void setBlkioDeviceWriteIOps(String blkioDeviceWriteIOps) {
        BlkioDeviceWriteIOps = blkioDeviceWriteIOps;
    }

    @JsonProperty("BlkioDeviceWriteIOps")
    private String BlkioDeviceWriteIOps;

    @JsonProperty("BlkioDeviceReadIOps")
    private String BlkioDeviceReadIOps;

    @JsonProperty("BlkioDeviceWriteBps")
    private String BlkioDeviceWriteBps;

    @JsonProperty("BlkioDeviceReadBps")
    private String BlkioDeviceReadBps;

    @JsonProperty("BlkioWeightDevice")
    private String BlkioWeightDevice;

    public String getPidsLimit() {
        return PidsLimit;
    }

    public void setPidsLimit(String pidsLimit) {
        PidsLimit = pidsLimit;
    }

    @JsonProperty("PidsLimit")

    private String PidsLimit;

    public String getBlkioWeightDevice() {
        return BlkioWeightDevice;
    }

    public void setBlkioWeightDevice(String blkioWeightDevice) {
        BlkioWeightDevice = blkioWeightDevice;
    }

    public String getIsolation() {
        return isolation;
    }

    public void setIsolation(String isolation) {
        this.isolation = isolation;
    }

    public Boolean getPrivileged() {
        return privileged;
    }

    public void setPrivileged(Boolean privileged) {
        this.privileged = privileged;
    }

    public Boolean getPublishAllPorts() {
        return publishAllPorts;
    }

    public void setPublishAllPorts(Boolean publishAllPorts) {
        this.publishAllPorts = publishAllPorts;
    }

    public List<String> getLinks() {
        return links;
    }

    public void setLinks(List<String> links) {
        this.links = links;
    }

    public List<String> getBinds() {
        return binds;
    }

    public void setBinds(List<String> binds) {
        this.binds = binds;
    }

    public Map<String, List<Object>> getPortBindings() {
        return portBindings;
    }

    public void setPortBindings(Map<String, List<Object>> portBindings) {
        this.portBindings = portBindings;
    }

    public List<String> getVolumesFrom() {
        return volumesFrom;
    }

    public void setVolumesFrom(List<String> volumesFrom) {
        this.volumesFrom = volumesFrom;
    }

    public Long getBlkioWeight() {
        return blkioWeight;
    }

    public void setBlkioWeight(Long blkioWeight) {
        this.blkioWeight = blkioWeight;
    }

    public String getCapAdd() {
        return capAdd;
    }

    public void setCapAdd(String capAdd) {
        this.capAdd = capAdd;
    }

    public String getCapDrop() {
        return capDrop;
    }

    public void setCapDrop(String capDrop) {
        this.capDrop = capDrop;
    }

    public String getContainerIDFile() {
        return containerIDFile;
    }

    public void setContainerIDFile(String containerIDFile) {
        this.containerIDFile = containerIDFile;
    }

    public String getCpusetCpus() {
        return cpusetCpus;
    }

    public void setCpusetCpus(String cpusetCpus) {
        this.cpusetCpus = cpusetCpus;
    }

    public String getCpusetMems() {
        return cpusetMems;
    }

    public void setCpusetMems(String cpusetMems) {
        this.cpusetMems = cpusetMems;
    }

    public Long getCpuShares() {
        return cpuShares;
    }

    public void setCpuShares(Long cpuShares) {
        this.cpuShares = cpuShares;
    }

    public Long getCpuPeriod() {
        return cpuPeriod;
    }

    public void setCpuPeriod(Long cpuPeriod) {
        this.cpuPeriod = cpuPeriod;
    }

    public List<String> getDevices() {
        return devices;
    }

    public void setDevices(List<String> devices) {
        this.devices = devices;
    }

    public String getExtraHosts() {
        return extraHosts;
    }

    public void setExtraHosts(String extraHosts) {
        this.extraHosts = extraHosts;
    }

    public String getIpcMode() {
        return ipcMode;
    }

    public void setIpcMode(String ipcMode) {
        this.ipcMode = ipcMode;
    }

    public List<String> getLxcConf() {
        return lxcConf;
    }

    public void setLxcConf(List<String> lxcConf) {
        this.lxcConf = lxcConf;
    }

    public Long getMemory() {
        return memory;
    }

    public void setMemory(Long memory) {
        this.memory = memory;
    }

    public Long getMemorySwap() {
        return memorySwap;
    }

    public void setMemorySwap(Long memorySwap) {
        this.memorySwap = memorySwap;
    }

    public Boolean getOomKillDisable() {
        return oomKillDisable;
    }

    public void setOomKillDisable(Boolean oomKillDisable) {
        this.oomKillDisable = oomKillDisable;
    }

    public String getNetworkMode() {
        return networkMode;
    }

    public void setNetworkMode(String networkMode) {
        this.networkMode = networkMode;
    }

    public Boolean getReadonlyRootfs() {
        return readonlyRootfs;
    }

    public void setReadonlyRootfs(Boolean readonlyRootfs) {
        this.readonlyRootfs = readonlyRootfs;
    }

    public String getSecurityOpt() {
        return securityOpt;
    }

    public void setSecurityOpt(String securityOpt) {
        this.securityOpt = securityOpt;
    }

    public List<Object> getUlimits() {
        return ulimits;
    }

    public void setUlimits(List<Object> ulimits) {
        this.ulimits = ulimits;
    }

    public String getCgroupParent() {
        return cgroupParent;
    }

    public void setCgroupParent(String cgroupParent) {
        this.cgroupParent = cgroupParent;
    }

    public String getPidMode() {
        return pidMode;
    }

    public void setPidMode(String pidMode) {
        this.pidMode = pidMode;
    }

    public RestartPolicy getRestartPolicy() {
        return restartPolicy;
    }

    public void setRestartPolicy(RestartPolicy restartPolicy) {
        this.restartPolicy = restartPolicy;
    }

    public LogConfig getLogConfig() {
        return logConfig;
    }

    public void setLogConfig(LogConfig logConfig) {
        this.logConfig = logConfig;
    }

    public Long getMemoryReservation() {
        return memoryReservation;
    }

    public void setMemoryReservation(Long memoryReservation) {
        this.memoryReservation = memoryReservation;
    }
}
