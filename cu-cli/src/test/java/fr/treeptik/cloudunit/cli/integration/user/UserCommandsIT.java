package fr.treeptik.cloudunit.cli.integration.user;

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
    public void test00_shouldConnect() {
        CommandResult result = connect();

        assertEquals(result.getResult().toString(), "Connection established");
    }

    @Test
    public void test01_shouldNotConnectCausedByAlreadyConnected() {
        connect();
        CommandResult result = connect();
        
        assertThat(result.getResult().toString(), containsString("You are already connected to CloudUnit servers"));
    }


}
