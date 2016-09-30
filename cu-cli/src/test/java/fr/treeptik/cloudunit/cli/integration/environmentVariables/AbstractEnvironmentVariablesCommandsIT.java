package fr.treeptik.cloudunit.cli.integration.environmentVariables;

import static fr.treeptik.cloudunit.cli.integration.ShellMatchers.*;
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
    public void test00_shouldCreateEnvironmentVariable() {
        connect();
        createApplication();
        try {
            CommandResult result = createEnvironmentVariable("key", "value");
    
            String expected = "An environment variable has been successfully added to " + applicationName.toLowerCase();
            Assert.assertEquals(expected, result.getResult().toString());
        } finally {
            removeApplication();
        }
    }

    private CommandResult createEnvironmentVariable(String key, String value) {
        return getShell().executeCommand(String.format("create-var-env --key %s --value %s", key, value));
    }

    @Test
    public void test01_shouldNotCreateEnvironmentVariableEmptyKey() {
        connect();
        createApplication();
        try {
            CommandResult result = createEnvironmentVariable("key", "value");
            assertThat(result, isFailedCommand());
        } finally {
            removeApplication();
        }
    }

    @Test
    public void test02_shouldNotCreateEnvironmentVariableForbiddenKey() {
        connect();
        createApplication();
        try {
            CommandResult result = createEnvironmentVariable("azérty", "value");
            assertThat(result, isFailedCommand());
        } finally {
            removeApplication();
        }
    }

    @Test
    public void test10_shouldRemoveEnvironmentVariable() {
        connect();
        createApplication();
        try {
            CommandResult result;
            
            result = createEnvironmentVariable("key", "value");
            assumeThat(result, isSuccessfulCommand());
            
            result = getShell().executeCommand("rm-var-env --key key");
            
            assertThat(result, isSuccessfulCommand());
            String expected = "This environment variable has successful been deleted";
            assertEquals(expected, result.getResult());
        } finally {
            removeApplication();
        }
    }

    @Test
    public void test11_shouldNotRemoveEnvironmentVariableEmptyKey() {
        connect();
        createApplication();
        try {
            CommandResult result;
            
            result = createEnvironmentVariable("key", "value");
            assumeThat(result, isSuccessfulCommand());
            
            result = getShell().executeCommand("rm-var-env --key");
            assertThat(result, isFailedCommand());
        } finally {
            removeApplication();
        }
    }

    @Test
    public void test12_shouldNotRemoveEnvironmentVariableUnexistingKey() {
        connect();
        createApplication();
        try {
            CommandResult result = getShell().executeCommand("rm-var-env --key azerty");

            String expected = String.format("An environment variable has been successfully added to %s", applicationName.toLowerCase());
            assertNotEquals(expected, result.getResult());
        } finally {
            removeApplication();
        }
        
    }

    @Test
    public void test20_shouldListEnvironmentVariables() {
        connect();
        createApplication();
        try {
            CommandResult result;
            result = createEnvironmentVariable("key", "value");
            result = getShell().executeCommand("list-var-env");
            
            String expected = "1 variables found!";
            assertEquals(expected, result.getResult().toString());
        } finally {
            removeApplication();
        }
    }

    @Test
    public void test30_shouldUpdateEnvironmentVariable() {
        connect();
        createApplication();
        try {
            CommandResult result;
            result = createEnvironmentVariable("key", "value");
            result = getShell().executeCommand("update-var-env --old-key key --new-key keyUpdated --value value");
            
            String expected = "This environment variable has successful been updated";
            assertEquals(expected, result.getResult().toString());
        } finally {
            removeApplication();
        }
    }

    @Test
    public void test31_shouldNotUpdateEnvironmentVariableEmptyOldKey() {
        connect();
        createApplication();
        try {
            CommandResult result;
            result = createEnvironmentVariable("key", "value");
            result = getShell().executeCommand("update-var-env --old-key --new-key key --value value");
            
            assertThat(result, isFailedCommand());
        } finally {
            removeApplication();
        }
    }

    @Test
    public void test32_shouldNotUpdateEnvironmentVariableEmptyNewKey() {
        connect();
        createApplication();
        try {
            CommandResult result;
            result = createEnvironmentVariable("key", "value");
            result = getShell().executeCommand("update-var-env --old-key key --new-key --value value");
            
            assertThat(result, isFailedCommand());
        } finally {
            removeApplication();
        }
    }

    @Test
    public void test33_shouldNotUpdateEnvironmentVariableUnexistingOldKey() {
        connect();
        createApplication();
        try {
            CommandResult result;
            result = createEnvironmentVariable("key", "value");
            result = getShell().executeCommand("update-var-env --old-key key2 --new-key key --value value");

            assertThat(result, isFailedCommand());
        } finally {
            removeApplication();
        }
    }

}
