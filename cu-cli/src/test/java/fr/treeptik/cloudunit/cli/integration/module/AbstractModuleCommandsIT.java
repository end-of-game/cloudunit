package fr.treeptik.cloudunit.cli.integration.module;

import static fr.treeptik.cloudunit.cli.integration.ShellMatchers.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.junit.Assume.*;

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
    public void test00_shouldAddMysqlModule() {
        test_addModule("mysql-5-5");
    }

    @Test
    public void test01_shouldAddPostGresModule() {
        test_addModule("postgresql-9-3");
    }

    @Test
    public void test10_shouldAddAndRemoveMysqlModule() {
        test_addAndRemoveModule("mysql-5-5");
    }

    @Test
    public void test11_shouldAddAndRemovePostGresModule() {
        test_addAndRemoveModule("postgresql-9-3");
    }

    @Test
    public void test03_shouldNotAddModuleBecauseImageDoesNotExist() {
        connect();
        createApplication();
        try {
            CommandResult result = addModule("mymodule");
            
            assertThat(result, isFailedCommand());
            
            String expected = "this module does not exist";
            assertEquals(expected, result.getResult().toString());
        } finally {
            removeApplication();
        }
    }

    @Test
    public void test04_shouldNotAddModuleBecauseApplicationNotSelected() {
        connect();
        CommandResult result = addModule("mysql-5-5");
        
        assertThat(result, isFailedCommand());
        
        String expected = "No application is currently selected by the following command line : use <application name>";
        assertThat(result.getResult().toString(), containsString(expected));
    }

    @Test
    public void test05_shouldNotAddModuleBecauseUserIsNotLogged() {
        CommandResult result = addModule("mysql-5-5");
        
        assertThat(result, isFailedCommand());
        
        String expected = "You are not connected to CloudUnit host! Please use connect command";
        assertThat(result.getResult().toString(), containsString(expected));
    }

    @Test
    public void test20_shouldListModules() {
        connect();
        createApplication();
        try {
            String result = addModule("mysql-5-5").getResult().toString();
            String expectedResult = String.format("Your module mysql-5-5 is currently being added to your application %s",
                    applicationName.toLowerCase());
            assertEquals(expectedResult, result);
            
            result = displayModules().getResult().toString();
            expectedResult = "1 modules found";
            assertEquals(expectedResult, result);
        } finally {
            removeApplication();
        }        
    }

    @Test
    public void test21_shouldNotListModulesBecauseApplicationNotSelected() {
        connect();
        String result = displayModules().getResult().toString();
        String expected = "No application is currently selected by the following command line : use <application name>";
        assertTrue(result.contains(expected));
    }

    @Test
    public void test22_shouldNotListModulesBecauseUserIsNotLogged() {
        String result = displayModules().getResult().toString();
        String expected = "You are not connected to CloudUnit host! Please use connect command";
        assertThat(result, containsString(expected));
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
            String result = addModule(moduleName).getResult().toString();
            String expected = String.format("Your module %s is currently being added to your application %s",
                    moduleName,
                    applicationName.toLowerCase());
            assertEquals(expected, result);
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
            String expected = String.format("Your module %s is currently being removed from your application %s",
                    moduleName,
                    applicationName.toLowerCase());
            assertEquals(expected, result.getResult().toString());
        } finally {
            removeApplication();
        }
    }

    private CommandResult runScript(String moduleName, String scriptPath) {
        return getShell().executeCommand(String.format("run-script --name %s --path %s", moduleName, scriptPath));
    }
}
