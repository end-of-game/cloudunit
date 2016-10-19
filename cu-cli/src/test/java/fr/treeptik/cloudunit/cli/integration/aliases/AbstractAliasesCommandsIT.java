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
    public void test_shouldAddAnAlias() {
        connect();
        createApplication();
        try {
            CommandResult result = addAlias();

            assertThat(result.getResult().toString(), containsString("added"));
            assertThat(result.getResult().toString(), containsString(applicationName.toLowerCase()));
        } finally {
            removeApplication();
            disconnect();
        }
    }

    @Test
    public void test_shouldAliasRemovingForbiddenExpression() {
        connect();
        createApplication();
        
        try {
            String alias = String.format("https://%s.eu", ALIAS);
            CommandResult result = addAlias(alias);
            
            assertThat(result, isSuccessfulCommand());
            assertThat(result.getResult().toString(), containsString("added"));
            assertThat(result.getResult().toString(), containsString(applicationName.toLowerCase()));
        } finally {
            removeApplication();
            disconnect();
        }        
    }

    @Test
    public void test_shouldNotAddAnAliasBecauseApplicationNotSelected() {
        connect();
        
        try {
            CommandResult result = addAlias();
            
            assertThat(result, isFailedCommand());
            assertThat(result.getException().getMessage(), containsString("not selected"));
        } finally {
            disconnect();
        }
    }

    @Test
    public void test_shouldNotAddAnAliasBecauseUserIsNotLogged() {
        CommandResult result = addAlias();
        
        assertThat(result, isFailedCommand());
        assertThat(result.getException().getMessage(), containsString("not connected"));
    }

    @Test
    public void test_shouldListAlias() {
        connect();
        createApplication();
        try {
            addAlias();
            CommandResult result = listAliasesCurrentApplication();
            
            assertThat(result, isSuccessfulCommand());
            assertThat(result.getResult().toString(), containsString("1"));
            assertThat(result.getResult().toString(), containsString("found"));
        } finally {
            removeApplication();
            disconnect();
        }
    }

    @Test
    public void test_shouldListAliasWithArgs() {
        connect();
        createApplication();
        try {
            addAlias();
            CommandResult result = listAliases();
            
            assertThat(result, isSuccessfulCommand());
            assertThat(result.getResult().toString(), containsString("1"));
            assertThat(result.getResult().toString(), containsString("found"));
        } finally {
            removeApplication();
            disconnect();
        }
    }

    @Test
    public void test_shouldNotListAliasBecauseUserIsNotLogged() {
        CommandResult result = listAliasesCurrentApplication();
        
        assertThat(result, isFailedCommand());
        assertThat(result.getException().getMessage(), containsString("not connected"));
    }

    @Test
    public void test_shouldNotListAliasBecauseApplicationNotSelected() {
        connect();
        try {
            CommandResult result = listAliasesCurrentApplication();
            
            assertThat(result, isFailedCommand());
            assertThat(result.getException().getMessage(), containsString("not selected"));
        } finally {
            disconnect();
        }
    }

    @Test
    public void test_shouldRemoveAliasWithArgs() {
        CommandResult result;
        
        connect();
        createApplication();
        addAlias();
        try {
            result = removeAlias();
            
            assertThat(result, isSuccessfulCommand());
            assertThat(result.getResult().toString(), containsString("removed"));
            assertThat(result.getResult().toString(), containsString(ALIAS));
            
            result = listAliases();
            assertThat(result.getResult().toString(), containsString("1 alias found"));
        } finally {
            removeApplication();
            disconnect();
        }
    }

    @Test
    public void test_shouldNotRemoveAliasBecauseApplicationNotSelected() {
        CommandResult result;
        
        connect();
        try {
            result = removeAlias(ALIAS);
            
            assertThat(result.getException().getMessage(), containsString("not selected"));
        } finally {
            disconnect();
        }
    }

    @Test
    public void test_shouldNotRemoveAliasBecauseUserIsNotLogged() {
        CommandResult result = removeAlias();
        
        assertThat(result, isFailedCommand());
        assertThat(result.getException().getMessage(), containsString("not connected"));
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

    private CommandResult listAliasesCurrentApplication() {
        return getShell().executeCommand("list-aliases");
    }
    
    private CommandResult listAliases() {
        return getShell().executeCommand(String.format("list-aliases --name %s", applicationName.toLowerCase()));
    }
}
