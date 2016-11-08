package fr.treeptik.cloudunit.aspects;

import fr.treeptik.cloudunit.config.events.DatabaseConnectionFailEvent;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

/**
 * Created by guillaume on 09/10/16.
 */
@Aspect
@Component
public class ErrorHandlerAspect {

    @Inject
    private ApplicationEventPublisher publisher;

    @AfterThrowing(pointcut = "execution(* fr.treeptik.cloudunit.service.*.*(..))",
            throwing= "error")
    public void logAfterThrowing(Throwable error) {
        if(error.getMessage() != null && error.getMessage()
                .contains("org.hibernate.exception.JDBCConnectionException: could not extract ResultSet")) {
            publisher.publishEvent(new DatabaseConnectionFailEvent("Database connection has been lost"));
        }
    }
}
