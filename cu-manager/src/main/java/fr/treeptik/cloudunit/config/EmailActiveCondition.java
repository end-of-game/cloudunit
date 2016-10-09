package fr.treeptik.cloudunit.config;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * Created by guillaume on 08/10/16.
 */

public class EmailActiveCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        return Boolean.valueOf(context.getEnvironment().getProperty("email.active"));
    }
}
