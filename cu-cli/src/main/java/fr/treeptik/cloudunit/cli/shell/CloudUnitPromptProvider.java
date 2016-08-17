package fr.treeptik.cloudunit.cli.shell;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.plugin.PromptProvider;
import org.springframework.stereotype.Component;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CloudUnitPromptProvider
        implements PromptProvider, CommandMarker {

    private static final String PROMPT_CU = "cloudunit";

    private static final String PROMPT_DELIMITER = "> ";

    @Value("${default.prompt}")
    private String prompt;

    private String cuInstanceName = "";

    private String applicationName = "";

    private void resetPrompt() {
        this.setPrompt(PROMPT_CU + cuInstanceName + applicationName + PROMPT_DELIMITER);
    }

    public String getProviderName() {
        return null;
    }

    public String getPrompt() {
        return prompt + " ";
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public void setCuInstanceName(String cuInstanceName) {
        this.cuInstanceName = cuInstanceName;
        resetPrompt();
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
        resetPrompt();
    }
}
