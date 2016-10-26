package fr.treeptik.cloudunit.cli.integration.module;

import static fr.treeptik.cloudunit.cli.integration.ShellMatchers.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.junit.Assume.*;

import org.junit.Assume;
import org.junit.Test;
import org.springframework.shell.core.CommandResult;

import fr.treeptik.cloudunit.cli.integration.AbstractShellIntegrationTest;

/**
 * Created by guillaume on 16/10/15.
 */
public abstract class AbstractModuleCommandsIT extends AbstractShellIntegrationTest {
    protected AbstractModuleCommandsIT(String serverType) {
        super(serverType);
    }

    @Test
    public void test_shouldAddMysqlModule() {
        test_addModule("mysql-5-5");
    }

    @Test
    public void test_shouldAddPostGresModule() {
        test_addModule("postgresql-9-3");
    }

    @Test
    public void test_shouldAddAndRemoveMysqlModule() {
        test_addAndRemoveModule("mysql-5-5");
    }

    @Test
    public void test_shouldAddAndRemovePostGresModule() {
        test_addAndRemoveModule("postgresql-9-3");
    }

    @Test
    public void test_shouldNotAddModuleBecauseImageDoesNotExist() {
        connect();
        createApplication();
        try {
            CommandResult result = addModule("mymodule");
            
            assertThat(result, isFailedCommand());
            assertThat(result.getException().getMessage(), containsString("No such image"));
            assertThat(result.getException().getMessage(), containsString("mymodule"));
        } finally {
            removeApplication();
        }
    }

    @Test
    public void test_shouldNotAddModuleBecauseApplicationNotSelected() {
        connect();
        try {
            CommandResult result = addModule("mysql-5-5");
            
            assertThat(result, isFailedCommand());
            assertThat(result.getException().getMessage(), containsString("not selected"));
        } finally {
            disconnect();
        }
    }

    @Test
    public void test_shouldNotAddModuleBecauseUserIsNotLogged() {
        CommandResult result = addModule("mysql-5-5");
        
        assertThat(result, isFailedCommand());
        assertThat(result.getException().getMessage(), containsString("not connected"));
    }

    @Test
    public void test_shouldListModules() {
        connect();
        createApplication();
        try {
            CommandResult result = addModule("mysql-5-5");
            Assume.assumeThat(result, isSuccessfulCommand());
            
            result = displayModules();
            
            assertThat(result, isSuccessfulCommand());
            assertThat(result.getResult().toString(), containsString("1"));
            assertThat(result.getResult().toString(), containsString("found"));
        } finally {
            removeApplication();
            disconnect();
        }
    }

    @Test
    public void test_shouldNotListModulesBecauseApplicationNotSelected() {
        connect();
        try {
            CommandResult result = displayModules();
            
            assertThat(result, isFailedCommand());
            assertThat(result.getException().getMessage(), containsString("not selected"));
        } finally {
            disconnect();
        }
    }

    @Test
    public void test_shouldNotListModulesBecauseUserIsNotLogged() {
        CommandResult result = displayModules();
        
        assertThat(result, isFailedCommand());
        assertThat(result.getException().getMessage(), containsString("not connected"));
    }

    @Test
    public void test_shouldRunScript() {
        String moduleName = "mysql-5-7";
        
        connect();
        createApplication();
        addModule(moduleName);
        try {
            CommandResult result = runScript(moduleName, "src/test/resources/test.sql");
            
            assertThat(result, isSuccessfulCommand());
        } finally {
            removeApplication();
        }
    }

    private void test_addModule(String moduleName) {
        connect();
        createApplication();
        try {
            CommandResult result = addModule(moduleName);

            assertThat(result, isSuccessfulCommand());
            assertThat(result.getResult().toString(), containsString("added"));
            assertThat(result.getResult().toString(), containsString(moduleName));
            assertThat(result.getResult().toString(), containsString(applicationName.toLowerCase()));
        } finally {
            removeApplication();
        }
    }
    
    private void test_addAndRemoveModule(String moduleName) {
        connect();
        createApplication();
        try {
            CommandResult result = addModule(moduleName);
            assumeThat(result, isSuccessfulCommand());
            
            result = removeModule(moduleName);
            
            assertThat(result, isSuccessfulCommand());
            assertThat(result.getResult().toString(), containsString("removed"));
            assertThat(result.getResult().toString(), containsString(moduleName));
            assertThat(result.getResult().toString(), containsString(applicationName.toLowerCase()));            
        } finally {
            removeApplication();
        }
    }

    private CommandResult runScript(String moduleName, String scriptPath) {
        return getShell().executeCommand(String.format("run-script --name %s --path %s", moduleName, scriptPath));
    }
}
