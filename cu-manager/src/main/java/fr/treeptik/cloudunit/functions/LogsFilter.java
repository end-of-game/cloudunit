package fr.treeptik.cloudunit.functions;

import fr.treeptik.cloudunit.dto.LogLine;

import java.util.Comparator;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

/**
 * Created by nicolas on 18/01/2016.
 */
public class LogsFilter {

    public static BiFunction<String, List<LogLine>, List<LogLine>> bySource =
            (source, messages) -> messages.stream()
                    .filter(m -> source.equals(m.getSource()))
                    .collect(Collectors.toList());

    public static BiFunction<String, List<LogLine>, List<LogLine>> byMessage =
            (pattern, messages) -> messages.stream()
                    .filter(m -> m.getMessage().toLowerCase().contains(pattern.toLowerCase()))
                    .collect(Collectors.toList());

}
