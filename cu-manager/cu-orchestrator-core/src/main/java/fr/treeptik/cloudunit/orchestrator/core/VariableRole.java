package fr.treeptik.cloudunit.orchestrator.core;

import java.security.SecureRandom;
import java.util.stream.Collectors;

public enum VariableRole {
    USER {
        @Override
        public String generateValue(Container container) {
            return container.getName();
        }
    },
    PASSWORD {
        @Override
        public String generateValue(Container container) {
            return new SecureRandom().ints(PASSWORD_BYTES / (2 * Integer.BYTES))
                    .mapToObj(i -> String.format("%x", i))
                    .collect(Collectors.joining());
        }
    },
    NAME {
        @Override
        public String generateValue(Container container) {
            return container.getName();
        }
    };
    
    private static final int PASSWORD_BYTES = 8;

    public abstract String generateValue(Container container);
}
