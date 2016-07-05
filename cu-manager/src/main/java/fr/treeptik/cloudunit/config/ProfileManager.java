package fr.treeptik.cloudunit.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Created by nicolas on 05/07/2016.
 */
@Component
public class ProfileManager {
    @Autowired
    Environment environment;

    @PostConstruct
    public void getActiveProfiles() {
        for (final String profileName : environment.getActiveProfiles()) {
            System.out.println("Currently active profile - " + profileName);
        }
    }
}
