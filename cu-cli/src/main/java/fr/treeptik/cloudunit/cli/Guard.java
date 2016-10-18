package fr.treeptik.cloudunit.cli;

import java.text.MessageFormat;

public class Guard {
    public static <T> void guardTrue(boolean condition, String message, Object... arguments) {
        if (!condition) {
            throw new CloudUnitCliException(MessageFormat.format(message, arguments));
        }
    }
}
