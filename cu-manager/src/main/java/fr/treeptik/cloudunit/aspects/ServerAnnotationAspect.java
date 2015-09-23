package fr.treeptik.cloudunit.aspects;

import fr.treeptik.cloudunit.model.User;
import fr.treeptik.cloudunit.service.UserService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import javax.inject.Inject;

/**
 * Created by nicolas on 31/08/15.
 */
@Aspect
public class ServerAnnotationAspect {

    @Inject
    private UserService userService;

    @After("@annotation(fr.treeptik.cloudunit.aspects.Loggable)")
    public void myAdvice(JoinPoint point) {

        try {
            UserDetails principal = (UserDetails) SecurityContextHolder
                    .getContext().getAuthentication().getPrincipal();
            User user = userService.findByLogin(principal.getUsername());
            System.out.println("Executing myAdvice!! " + user);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

}
