package fr.treeptik.cloudunit.utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.treeptik.cloudunit.docker.builders.ConfigBuilder;
import fr.treeptik.cloudunit.docker.builders.ContainerBuilder;
import fr.treeptik.cloudunit.docker.builders.HostConfigBuilder;
import fr.treeptik.cloudunit.docker.model.Config;
import fr.treeptik.cloudunit.docker.model.DockerContainer;
import fr.treeptik.cloudunit.docker.model.HostConfig;

/**
 * Created by guillaume on 11/01/16.
 */
public class ContainerUtils {

    public static DockerContainer newCreateInstance(String name, String image, List<String> volumesFrom,
            List<String> args, List<String> rawVolumes, List<String> envs,
            Map<String, String> ports, String networkMode) {
        HostConfig hostConfig = HostConfigBuilder.aHostConfig().withVolumesFrom(volumesFrom).withBinds(rawVolumes)
                .withPublishAllPorts(false).withPortBindings(buildPortBindingBody(ports))
                .withNetworkMode(networkMode)
                .build();
        Config config = ConfigBuilder.aConfig().withAttachStdin(Boolean.FALSE).withAttachStdout(Boolean.TRUE)
                .withAttachStderr(Boolean.TRUE).withCmd(args).withImage(image).withHostConfig(hostConfig).withMemory(0L)
                .withMemorySwap(0L).withEnv(envs).build();
        Map<String, String> labels = new HashMap<>();
        labels.put("traefik.port", "8080");
        labels.put("traefik.backend", name);
        labels.put("traefik.frontend.rule", "Host:"+name+".cloudunit.dev");
        config.setLabels(labels);
        DockerContainer container = ContainerBuilder.aContainer().withName(name).withConfig(config).build();
        return container;
    }

    public static DockerContainer newStartInstance(String name, List<String> volumesFrom, List<String> volumes,
            Boolean publishAllPorts) {
        HostConfig hostConfig = HostConfigBuilder.aHostConfig().withBinds(volumes).withPrivileged(Boolean.FALSE)
                .withPublishAllPorts(publishAllPorts).withVolumesFrom(volumesFrom).build();
        Config config = ConfigBuilder.aConfig().withHostConfig(hostConfig).build();
        DockerContainer container = ContainerBuilder.aContainer().withName(name).withConfig(config).build();
        return container;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static Map<String, List<Map<String, String>>> buildPortBindingBody(Map<String, String> ports) {
        if (ports == null) {
            return null;
        }
        Map finalMap = new HashMap();
            for (String port : ports.keySet()) {
                Map<String, String> mapForHostPort = new HashMap() {
                    private static final long serialVersionUID = 1L;
                    {
                        put("HostPort", ports.get(port));
                    }
                };
                List<Map<String, String>> params = Arrays.asList(mapForHostPort);
                finalMap.put(port, params);
            }
        return finalMap;

    }
}
