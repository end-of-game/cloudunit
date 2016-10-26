package fr.treeptik.cloudunit.docker.builders;

import fr.treeptik.cloudunit.docker.model.Config;
import fr.treeptik.cloudunit.docker.model.DockerContainer;
import fr.treeptik.cloudunit.docker.model.HostConfig;

import java.util.List;
import java.util.Map;

/**
 * Created by guillaume on 21/10/15.
 */
public class ContainerBuilder {
    private Config config;
    private String appArmorProfile;
    private List<String> args;
    private String created;
    private String driver;
    private String execDriver;
    private List<String> execIDs;
    private HostConfig hostConfig;
    private String hostnamePath;
    private String hostsPath;
    private String logPath;
    private String id;
    private String image;
    private String mountLabel;
    private String name;
    private String path;
    private String processLabel;
    private String resolvConfPath;
    private Long restartCount;
    private Map<String, String> volumes;
    private Map<String, String> volumesRW;

    private ContainerBuilder() {
    }

    public static ContainerBuilder aContainer() {
        return new ContainerBuilder();
    }

    public ContainerBuilder withConfig(Config config) {
        this.config = config;
        return this;
    }

    public ContainerBuilder withAppArmorProfile(String appArmorProfile) {
        this.appArmorProfile = appArmorProfile;
        return this;
    }

    public ContainerBuilder withArgs(List<String> args) {
        this.args = args;
        return this;
    }

    public ContainerBuilder withCreated(String created) {
        this.created = created;
        return this;
    }

    public ContainerBuilder withDriver(String driver) {
        this.driver = driver;
        return this;
    }

    public ContainerBuilder withExecDriver(String execDriver) {
        this.execDriver = execDriver;
        return this;
    }

    public ContainerBuilder withExecIDs(List<String> execIDs) {
        this.execIDs = execIDs;
        return this;
    }

    public ContainerBuilder withHostConfig(HostConfig hostConfig) {
        this.hostConfig = hostConfig;
        return this;
    }

    public ContainerBuilder withHostnamePath(String hostnamePath) {
        this.hostnamePath = hostnamePath;
        return this;
    }

    public ContainerBuilder withHostsPath(String hostsPath) {
        this.hostsPath = hostsPath;
        return this;
    }

    public ContainerBuilder withLogPath(String logPath) {
        this.logPath = logPath;
        return this;
    }

    public ContainerBuilder withId(String id) {
        this.id = id;
        return this;
    }

    public ContainerBuilder withImage(String image) {
        this.image = image;
        return this;
    }

    public ContainerBuilder withMountLabel(String mountLabel) {
        this.mountLabel = mountLabel;
        return this;
    }

    public ContainerBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public ContainerBuilder withPath(String path) {
        this.path = path;
        return this;
    }

    public ContainerBuilder withProcessLabel(String processLabel) {
        this.processLabel = processLabel;
        return this;
    }

    public ContainerBuilder withResolvConfPath(String resolvConfPath) {
        this.resolvConfPath = resolvConfPath;
        return this;
    }

    public ContainerBuilder withRestartCount(Long restartCount) {
        this.restartCount = restartCount;
        return this;
    }

    public ContainerBuilder withVolumes(Map<String, String> volumes) {
        this.volumes = volumes;
        return this;
    }

    public ContainerBuilder withVolumesRW(Map<String, String> volumesRW) {
        this.volumesRW = volumesRW;
        return this;
    }

    public ContainerBuilder but() {
        return aContainer().withConfig(config).withAppArmorProfile(appArmorProfile).withArgs(args).withCreated(created).withDriver(driver).withExecDriver(execDriver).withExecIDs(execIDs).withHostConfig(hostConfig).withHostnamePath(hostnamePath).withHostsPath(hostsPath).withLogPath(logPath).withId(id).withImage(image).withMountLabel(mountLabel).withName(name).withPath(path).withProcessLabel(processLabel).withResolvConfPath(resolvConfPath).withRestartCount(restartCount).withVolumes(volumes).withVolumesRW(volumesRW);
    }

    public DockerContainer build() {
        DockerContainer container = new DockerContainer();
        container.setConfig(config);
        container.setAppArmorProfile(appArmorProfile);
        container.setArgs(args);
        container.setCreated(created);
        container.setDriver(driver);
        container.setExecDriver(execDriver);
        container.setExecIDs(execIDs);
        container.setHostConfig(hostConfig);
        container.setHostnamePath(hostnamePath);
        container.setHostsPath(hostsPath);
        container.setLogPath(logPath);
        container.setId(id);
        container.setImage(image);
        container.setMountLabel(mountLabel);
        container.setName(name);
        container.setPath(path);
        container.setProcessLabel(processLabel);
        container.setResolvConfPath(resolvConfPath);
        container.setRestartCount(restartCount);
        container.setVolumes(volumes);
        container.setVolumesRW(volumesRW);
        return container;
    }
}
