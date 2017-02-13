package fr.treeptik.cloudunit.cli.utils;

import java.util.Optional;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.treeptik.cloudunit.cli.CloudUnitCliException;
import fr.treeptik.cloudunit.cli.Messages;
import fr.treeptik.cloudunit.cli.exception.ManagerResponseException;
import fr.treeptik.cloudunit.cli.processor.InjectLogger;
import fr.treeptik.cloudunit.cli.rest.JsonConverter;
import fr.treeptik.cloudunit.cli.rest.RestUtils;
import fr.treeptik.cloudunit.dto.AboutResource;

@Component
public class AboutUtils {
    private static final String NO_INFORMATION = Messages.getString("about.NO_INFORMATION");
    
    @InjectLogger
    private Logger log;
    
    @Autowired
    private AuthenticationUtils authentificationUtils;
    
    @Autowired
    private UrlLoader urlLoader;
    
    @Autowired
    private RestUtils restUtils;
    
    public Optional<AboutResource> getAbout() {
        if (authentificationUtils.isConnected()) {
            try {
                String url = authentificationUtils.finalHost + urlLoader.aboutPrefix;
                String result = restUtils.sendGetCommand(url, authentificationUtils.getMap())
                        .get(RestUtils.BODY);
                AboutResource aboutApi = JsonConverter.getAbout(result);
                return Optional.of(aboutApi);
            } catch (ManagerResponseException e) {
                throw new CloudUnitCliException(NO_INFORMATION, e);
            }
        } else {
            return Optional.empty();
        }
    }

}
