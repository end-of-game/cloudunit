package fr.treeptik.cloudunit.cli.commands;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.stereotype.Component;

import fr.treeptik.cloudunit.cli.utils.AboutUtils;
import fr.treeptik.cloudunit.dto.AboutResource;

@Component
public class AboutCommands implements CommandMarker {
    @Autowired
    private AboutUtils aboutUtils;
    
    @Value("${cloudunit.cli.version}")
    private String version;
    
    @Value("${cloudunit.cli.timestamp}")
    private String timestamp;
    
    @CliCommand(value = "about", help = "Print information about CloudUnit Manager")
    public String about() {
        Optional<AboutResource> aboutApi = aboutUtils.getAbout();
        
        return aboutApi
                .map(this::formatAbout)
                .orElse(formatAbout());
    }
    
    private String formatAbout(AboutResource aboutApi) {
        return String.format("%s%nCloudUnit Manager API version %s (build timestamp %s)",
                formatAbout(),
                aboutApi.getVersion(),
                aboutApi.getTimestamp());
    }
    
    private String formatAbout() {
        return String.format("CloudUnit CLI version %s (build timestamp %s)",
                version,
                timestamp);        
    }
}
