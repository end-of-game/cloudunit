package fr.treeptik.cloudunit.factory;

import fr.treeptik.cloudunit.dto.LogResource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by nicolas on 20/09/2016.
 */
public class LogResourceFactory {

    private static LogResource fromStdout(String line) {
        LogResource logResource = new LogResource("stdout", line);
        return logResource;
    }

    public static List<LogResource> fromOutput(String outputShell) {
        if (outputShell == null) return new ArrayList<>();
        if(outputShell.trim().length()<=3) return new ArrayList<>();
        outputShell = outputShell.trim();
        List<LogResource> logResources = Stream.of(outputShell)
                .map(line -> line.split("\\n"))
                .flatMap(Arrays::stream)
                .map(LogResourceFactory::fromStdout)
                .collect(Collectors.toList());
        Collections.reverse(logResources);
        return logResources;
    }
}
