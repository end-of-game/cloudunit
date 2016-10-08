package fr.treeptik.cloudunit.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.stereotype.Component;

/**
 * Created by guillaume on 08/10/16.
 */
@Component
public class EmailActiveCondition implements Condition {

    @Value("${email.active:false}")
    private Boolean sendEmailActive;

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {

        if(sendEmailActive==null){
            return false;
        }

        return sendEmailActive;
    }
}
