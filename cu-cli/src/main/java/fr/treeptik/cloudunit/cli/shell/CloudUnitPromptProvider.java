package fr.treeptik.cloudunit.cli.shell;

import java.text.MessageFormat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.plugin.PromptProvider;
import org.springframework.stereotype.Component;

import fr.treeptik.cloudunit.cli.commands.CliFormatter;
import fr.treeptik.cloudunit.cli.utils.ApplicationUtils;
import fr.treeptik.cloudunit.cli.utils.AuthenticationUtils;
import fr.treeptik.cloudunit.cli.utils.FileUtils;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CloudUnitPromptProvider implements PromptProvider, CommandMarker {
    private static final String APPLICATION_NAME = "cloudunit";
    
    private static final String PROMPT_NOT_CONNECTED = "{0}> ";
    private static final String PROMPT_CONNECTED = "{0} {1}> ";
    private static final String PROMPT_APPLICATION_SELECTED = "{0} {1} {2}> ";
    private static final String PROMPT_EXPLORER = "{0} {1} [{2}]> ";
    
    @Autowired
    private CliFormatter formatter;

    @Autowired
    private AuthenticationUtils authenticationUtils;
    
    @Autowired
    private ApplicationUtils applicationUtils;
    
    @Autowired
    private FileUtils fileUtils;
    
    @Override
    public String getProviderName() {
        return "cloudunit";
    }

    @Override
    public String getPrompt() {
        if (formatter.isQuiet()) {
            return "";
        } else if (!authenticationUtils.isConnected()) {
            return MessageFormat.format(PROMPT_NOT_CONNECTED, APPLICATION_NAME);
        } else if (!applicationUtils.isApplicationSelected()) {
            return MessageFormat.format(PROMPT_CONNECTED,
                    APPLICATION_NAME,
                    authenticationUtils.getCurrentInstanceName());
        } else if (!fileUtils.isInFileExplorer()) {
            return MessageFormat.format(PROMPT_APPLICATION_SELECTED,
                    APPLICATION_NAME,
                    authenticationUtils.getCurrentInstanceName(),
                    applicationUtils.getCurrentApplication().getName());
        } else {
            return MessageFormat.format(PROMPT_EXPLORER,
                    APPLICATION_NAME,
                    authenticationUtils.getCurrentInstanceName(),
                    fileUtils.getCurrentContainerName());
        }
    }
}
