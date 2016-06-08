package fr.treeptik.cloudunit.factory;

import fr.treeptik.cloudunit.dto.EnvUnit;

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
        List<EnvUnit> envUnits = Stream.of(outputShell)
                .map(line -> line.split("\\n"))
                .flatMap(Arrays::stream)
                .filter(line -> line.contains("CU_"))
                .map(EnvUnitFactory::fromLine)
                .collect(Collectors.toList());
        return envUnits;
    }

}
