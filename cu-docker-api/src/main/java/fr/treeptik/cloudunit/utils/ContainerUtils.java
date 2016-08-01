package fr.treeptik.cloudunit.utils;

import fr.treeptik.cloudunit.docker.builders.ConfigBuilder;
import fr.treeptik.cloudunit.docker.builders.ContainerBuilder;
import fr.treeptik.cloudunit.docker.builders.HostConfigBuilder;
import fr.treeptik.cloudunit.docker.model.Config;
import fr.treeptik.cloudunit.docker.model.DockerContainer;
import fr.treeptik.cloudunit.docker.model.HostConfig;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by guillaume on 11/01/16.
 */
public class ContainerUtils {


    public static DockerContainer newCreateInstance(String name, String image,
                                                    List<String> volumesFrom, List<String> args) {
        HostConfig hostConfig = HostConfigBuilder.aHostConfig()
                .withVolumesFrom(volumesFrom)
                .build();
        Config config = ConfigBuilder.aConfig()
                .withAttachStdin(Boolean.FALSE)
                .withAttachStdout(Boolean.TRUE)
                .withAttachStderr(Boolean.TRUE)
                .withCmd(args)
                .withImage(image)
                .withHostConfig(hostConfig)
                .withMemory(0L)
                .withMemorySwap(0L)
                .build();
        DockerContainer container = ContainerBuilder.aContainer()
                .withName(name)
                .withConfig(config)
                .build();
        return container;
    }

    public static DockerContainer newStartInstance(String name,
                                                   Map<String, String> ports,
                                                   List<String> volumesFrom,
                                                   List<String> volumes) {
        HostConfig hostConfig = HostConfigBuilder.aHostConfig()
                .withBinds(volumes)
                .withPortBindings(buildPortBindingBody(ports))
                .withPrivileged(Boolean.FALSE)
                .withPublishAllPorts(Boolean.FALSE)
                .withVolumesFrom(volumesFrom).build();
        Config config = ConfigBuilder.aConfig()
                .withHostConfig(hostConfig)
                .build();
        DockerContainer container = ContainerBuilder.aContainer()
                .withName(name)
                .withConfig(config).build();
        return container;
    }

    private static Map buildPortBindingBody(Map<String, String> ports) {
        Map finalMap = new HashMap();
        if (ports != null) {
            for (String port : ports.keySet()) {
                Map<String, String> mapForHostPort = new HashMap() {{
                    put("HostPort", ports.get(port));
                }};
                List<Map<String, String>> params = Arrays.asList(mapForHostPort);
                finalMap.put(port, params);
            }
        }
        return finalMap;

    }
}
