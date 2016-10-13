package fr.treeptik.cloudunit.functions;

import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import fr.treeptik.cloudunit.dto.LogResource;

/**
 * Created by nicolas on 18/01/2016.
 */
public class LogsFilter {

    public static BiFunction<String, List<LogResource>, List<LogResource>> bySource =
            (source, messages) -> messages.stream()
                    .filter(m -> source.equals(m.getSource()))
                    .collect(Collectors.toList());

    public static BiFunction<String, List<LogResource>, List<LogResource>> byMessage =
            (pattern, messages) -> messages.stream()
                    .filter(m -> m.getMessage().toLowerCase().contains(pattern.toLowerCase()))
                    .collect(Collectors.toList());

}
