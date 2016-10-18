package fr.treeptik.cloudunit.cli.integration.environmentVariables;

import static fr.treeptik.cloudunit.cli.integration.ShellMatchers.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.junit.Assume.*;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.shell.core.CommandResult;

import fr.treeptik.cloudunit.cli.integration.AbstractShellIntegrationTest;

public class AbstractEnvironmentVariablesCommandsIT extends AbstractShellIntegrationTest {
    
    protected AbstractEnvironmentVariablesCommandsIT(String serverType) {
        super(serverType);
    }

    @Test
    public void test_shouldCreateEnvironmentVariable() {
        connect();
        createApplication();
        try {
            CommandResult result = createEnvironmentVariable("key", "value");
    
            Assert.assertThat(result, isSuccessfulCommand());
            Assert.assertThat(result.getResult().toString(), containsString("added"));
            Assert.assertThat(result.getResult().toString(), containsString("key"));
            Assert.assertThat(result.getResult().toString(), containsString(applicationName.toLowerCase()));
        } finally {
            removeApplication();
            disconnect();
        }
    }

    @Test
    public void test_shouldNotCreateEnvironmentVariableEmptyKey() {
        connect();
        createApplication();
        try {
            CommandResult result = createEnvironmentVariable("", "value");
            assertThat(result, isFailedCommand());
        } finally {
            removeApplication();
            disconnect();
        }
    }

    @Test
    public void test_shouldNotCreateEnvironmentVariableForbiddenKey() {
        connect();
        createApplication();
        try {
            CommandResult result = createEnvironmentVariable("azérty", "value");
            
            assertThat(result, isFailedCommand());
        } finally {
            removeApplication();
            disconnect();
        }
    }

    @Test
    public void test_shouldRemoveEnvironmentVariable() {
        connect();
        createApplication();
        try {
            CommandResult result;
            
            result = createEnvironmentVariable("key", "value");
            assumeThat(result, isSuccessfulCommand());
            
            result = removeEnvironmentVariable("key");
            
            assertThat(result, isSuccessfulCommand());
            assertThat(result.getResult().toString(), containsString("removed"));
            assertThat(result.getResult().toString(), containsString("key"));
            assertThat(result.getResult().toString(), containsString(applicationName.toLowerCase()));
        } finally {
            removeApplication();
            disconnect();
        }
    }

    @Test
    public void test_shouldNotRemoveEnvironmentVariableEmptyKey() {
        connect();
        createApplication();
        try {
            CommandResult result;
            
            result = createEnvironmentVariable("key", "value");
            assumeThat(result, isSuccessfulCommand());
            
            result = removeEnvironmentVariable("");
            assertThat(result, isFailedCommand());
        } finally {
            removeApplication();
            disconnect();
        }
    }

    @Test
    public void test_shouldNotRemoveEnvironmentVariableUnexistingKey() {
        connect();
        createApplication();
        try {
            CommandResult result = removeEnvironmentVariable("azerty");

            String expected = String.format("An environment variable has been successfully added to %s", applicationName.toLowerCase());
            assertNotEquals(expected, result.getResult());
        } finally {
            removeApplication();
            disconnect();
        }
        
    }

    @Test
    public void test_shouldListEnvironmentVariables() {
        connect();
        createApplication();
        try {
            createEnvironmentVariable("key", "value");
            CommandResult result = listEnvironmentVariables();
            
            String expected = "1 variables found!";
            assertEquals(expected, result.getResult().toString());
        } finally {
            removeApplication();
            disconnect();
        }
    }

    @Test
    public void test_shouldUpdateEnvironmentVariable() {
        connect();
        createApplication();
        try {
            createEnvironmentVariable("key", "value");
            CommandResult result = updateEnvironmentVariable("key", "keyUpdated", "value");
            
            String expected = "This environment variable has successful been updated";
            assertEquals(expected, result.getResult().toString());
        } finally {
            removeApplication();
        }
    }

    @Test
    public void test_shouldNotUpdateEnvironmentVariableEmptyOldKey() {
        connect();
        createApplication();
        try {
            createEnvironmentVariable("key", "value");
            CommandResult result = updateEnvironmentVariable("", "key", "value");
            
            assertThat(result, isFailedCommand());
        } finally {
            removeApplication();
        }
    }

    @Test
    public void test_shouldNotUpdateEnvironmentVariableEmptyNewKey() {
        connect();
        createApplication();
        try {
            createEnvironmentVariable("key", "value");
            CommandResult result = updateEnvironmentVariable("key", "", "value");
            
            assertThat(result, isFailedCommand());
        } finally {
            removeApplication();
        }
    }

    @Test
    public void test_shouldNotUpdateEnvironmentVariableUnexistingOldKey() {
        connect();
        createApplication();
        try {
            createEnvironmentVariable("key", "value");
            CommandResult result = updateEnvironmentVariable("key2", "key", "value");

            assertThat(result, isFailedCommand());
        } finally {
            removeApplication();
        }
    }

    private CommandResult createEnvironmentVariable(String key, String value) {
        return getShell().executeCommand(String.format("create-var-env --key %s --value %s", key, value));
    }
    
    private CommandResult removeEnvironmentVariable(String key) {
        return getShell().executeCommand(String.format("rm-var-env --key %s", key));
    }
    
    private CommandResult updateEnvironmentVariable(String oldKey, String newKey, String value) {
        return getShell().executeCommand(String.format("update-var-env --old-key %s --new-key %s --value %s",
                oldKey,
                newKey,
                value));
    }
    
    private CommandResult listEnvironmentVariables() {
        return getShell().executeCommand("list-var-env");
    }
}
