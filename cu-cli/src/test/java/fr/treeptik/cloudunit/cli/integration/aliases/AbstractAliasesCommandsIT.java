package fr.treeptik.cloudunit.cli.integration.aliases;

import static fr.treeptik.cloudunit.cli.integration.ShellMatchers.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.junit.Test;
import org.springframework.shell.core.CommandResult;

import fr.treeptik.cloudunit.cli.integration.AbstractShellIntegrationTest;

/**
 * Created by guillaume on 16/10/15.
 */
public abstract class AbstractAliasesCommandsIT extends AbstractShellIntegrationTest {
    private static final String ALIAS = "myalias.cloudunit.dev";
    
    protected AbstractAliasesCommandsIT(String serverType) {
        super(serverType);
    }

    @Test
    public void test00_shouldAddAnAlias() {
        connect();
        createApplication();
        try {
            CommandResult result = addAlias();
            String expected = "An alias has been successfully added to " + applicationName.toLowerCase();
            
            assertEquals(expected, result.getResult().toString());
        } finally {
            removeApplication();
        }
    }

    @Test
    public void test01_shouldAliasRemovingForbiddenExpression() {
        connect();
        createApplication();
        
        try {
            String alias = String.format("https://%s.eu", ALIAS);
            CommandResult result = addAlias(alias);
            
            assertThat(result, isSuccessfulCommand());
            String expected = "An alias has been successfully added to " + applicationName.toLowerCase();
            assertEquals(expected, result.getResult().toString());
        } finally {
            removeApplication();
        }        
    }

    @Test
    public void test02_shouldNotAddAnAliasBecauseApplicationNotSelected() {
        connect();
        
        CommandResult result = addAlias();
        
        assertThat(result, isFailedCommand());
        String expected = "No application is currently selected by the following command line : use <application name>";
        assertThat(result.getException().getMessage(), containsString(expected));
    }

    @Test
    public void test03_shouldNotAddAnAliasBecauseUserIsNotLogged() {
        CommandResult result = addAlias();
        
        assertThat(result, isFailedCommand());
        String expected = "You are not connected to CloudUnit host! Please use connect command";
        assertThat(result.getException().getMessage(), containsString(expected));
    }

    @Test
    public void test10_shouldListAlias() {
        connect();
        createApplication();
        try {
            addAlias();
            CommandResult result = listAliases();
            
            assertEquals("1 aliases found!", result.getResult().toString());
        } finally {
            removeApplication();
        }
    }

    @Test
    public void test11_shouldListAliasWithArgs() {
        connect();
        CommandResult result = listAliasesForApplication();
        
        assertEquals("2 aliases found!", result.getResult().toString());
    }

    private CommandResult listAliasesForApplication() {
        return getShell().executeCommand("list-aliases --name " + applicationName);
    }

    @Test
    public void test12_shoudNotListAliasBecauseUserIsNotLogged() {
        CommandResult result = listAliases();
        
        assertThat(result.getResult().toString(),
                containsString("You are not connected to CloudUnit host! Please use connect command"));
    }

    @Test
    public void test12_shoudNotListAliasBecauseApplicationNotSelected() {
        connect();
        CommandResult result = listAliases();
        
        assertThat(result.getResult().toString(),
                containsString("No application is currently selected"));
    }

    @Test
    public void test20_shouldRemoveAliasWithArgs() {
        CommandResult result;
        
        connect();
        createApplication();
        addAlias();
        try {
            result = removeAlias();
            assertEquals("This alias has successful been deleted", result.getResult().toString());
            result = listAliasesForApplication();
            assertEquals("1 aliases found!", result.getResult().toString());
        } finally {
            removeApplication();
        }
    }

    @Test
    public void test21_shouldNotRemoveAliasBecauseApplicationNotSelected() {
        CommandResult result;
        
        connect();
        createApplication();
        try {
            result = removeAlias(ALIAS);
            
            assertThat(result.getResult().toString(), containsString("No application is currently selected"));
            
            result = listAliasesForApplication();
            
            assertEquals("1 aliases found!", result.getResult().toString());
        } finally {
            removeApplication();
        }
    }

    @Test
    public void test22_shouldNotRemoveAliasBecauseUserIsNotLogged() {
        CommandResult result = removeAlias();
        assertThat(result, isFailedCommand());
        assertThat(
                result.getResult().toString(),
                containsString("You are not connected to CloudUnit host! Please use connect command"));
    }

    private CommandResult addAlias() {
        return addAlias(ALIAS);
    }
    
    private CommandResult addAlias(String alias) {
        return getShell().executeCommand(String.format("add-alias --alias %s", alias));
    }
    
    private CommandResult removeAlias() {
        return removeAlias(ALIAS);
    }
    
    private CommandResult removeAlias(String alias) {
        return getShell().executeCommand(String.format("rm-alias --alias %s", alias));
    }

    private CommandResult listAliases() {
        return getShell().executeCommand("list-aliases");
    }
}
