package fr.treeptik.cloudunit.factory;

import fr.treeptik.cloudunit.dto.EnvUnit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by nicolas on 07/06/2016.
 */
public class EnvUnitFactory {

    public static EnvUnit fromLine(String line) {
        String[] tokens = line.split("=");
        EnvUnit envUnit = new EnvUnit(tokens[0], tokens[1]);
        return envUnit;
    }

    public static List<EnvUnit> fromOutput(String outputShell) {
        if (outputShell == null) return new ArrayList<>();
        if(outputShell.trim().length()<=3) return new ArrayList<>();
        outputShell = outputShell.trim();
        List<EnvUnit> envUnits = Stream.of(outputShell)
                .map(line -> line.split("\\n"))
                .flatMap(Arrays::stream)
                .map(EnvUnitFactory::fromLine)
                .sorted((k1, k2) -> k1.getKey().compareTo(k2.getKey()))
                .collect(Collectors.toList());
        return envUnits;
    }

}
