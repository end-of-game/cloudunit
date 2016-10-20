package fr.treeptik.cloudunit.cli.integration.user;

import static fr.treeptik.cloudunit.cli.integration.ShellMatchers.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.junit.Test;
import org.springframework.shell.core.CommandResult;

import fr.treeptik.cloudunit.cli.integration.AbstractShellIntegrationTest;

/**
 * Created by guillaume on 15/10/15.
 */
public class UserCommandsIT extends AbstractShellIntegrationTest {
    public UserCommandsIT() {
        super("tomcat-8");
    }

    @Test
    public void test_shouldConnect() {
        CommandResult result = connect();

        assertThat(result, isSuccessfulCommand());
        assertEquals(result.getResult().toString(), "Connection established");
    }

    @Test
    public void test_shouldNotConnectCausedByAlreadyConnected() {
        connect();
        CommandResult result = connect();
        
        assertThat(result, isFailedCommand());
        assertThat(result.getException().getMessage(), containsString("already connected"));
    }

}
