package fr.treeptik.cloudunit.cli.integration.application;

import static fr.treeptik.cloudunit.cli.integration.ShellMatchers.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.junit.Test;
import org.springframework.shell.core.CommandResult;

import fr.treeptik.cloudunit.cli.integration.AbstractShellIntegrationTest;

/**
 * Created by guillaume on 16/10/15.
 */
public abstract class AbstractApplicationCommandsIT extends AbstractShellIntegrationTest {

    protected AbstractApplicationCommandsIT(String serverType) {
        super(serverType);
    }

    @Test
    public void test_shouldCreateApplication() {
        connect();
        CommandResult result = createApplication();
        
        assertThat(result, isSuccessfulCommand());
        
        removeCurrentApplication();
        disconnect();
    }

    @Test
    public void test_shouldNotCreateApplicationBecauseUserIsNotLogged() {
        CommandResult result = createApplication();
        
        assertThat(result, isFailedCommand());
        assertThat(result.getException().getMessage(), containsString("You are not connected"));
    }

    @Test
    public void test_shouldNotCreateApplicationBecauseNameAlreadyInUse() {
        connect();
        createApplication();
        try {
            CommandResult result = createApplication();
            
            assertThat(result, isFailedCommand());
            assertThat(result.getException().getMessage(), containsString("This application name already exists"));
        } finally {
            removeApplication();
            disconnect();
        }
    }

    @Test
    public void test_shouldNotCreateApplicationBecauseServerDoesNotExists() {
        connect();
        try {
            CommandResult result = createApplication(applicationName, "xxx");
            
            assertThat(result, isFailedCommand());
            assertThat(result.getException().getMessage(), containsString("This server image does not exist"));
        } finally {
            disconnect();
        }
    }

    @Test
    public void test_shouldNotCreateApplicationNonAlphaNumericCharsBecauseApplicationAlreadyExists() {
        connect();
        createApplication(applicationName);
        try {
            CommandResult result = createApplication(applicationName+"&~~");
            
            assertThat(result, isFailedCommand());
            assertThat(result.getException().getMessage(), containsString("This application name already exists"));
        } finally {
            removeApplication();
            disconnect();
        }
    }

    @Test
    public void test_shouldSelectAnApplication() {
        connect();
        createApplication();
        try {
            disconnect();
            
            connect();
            CommandResult result = useApplication(applicationName);
            
            String expected = String.format("Current application : %s", applicationName.toLowerCase());
            assertEquals(expected, result.getResult().toString());
        } finally {
            connect();
            removeApplication();
            disconnect();
        }
    }

    @Test
    public void test_shouldNotSelectAnApplicationBecauseUserIsNotLogged() {
        CommandResult result = useApplication(applicationName);
        
        assertThat(result, isFailedCommand());
        String expected = "You are not connected";
        assertThat(result.getException().getMessage(), containsString(expected));
    }

    @Test
    public void test_shouldNotSelectAnApplicationBecauseDoesNotExist() {
        connect();
        try {
            CommandResult result = useApplication("zqdmokdzq");
            
            assertThat(result, isFailedCommand());
            String expected = "This application does not exist on this account";
            assertThat(result.getException().getMessage(), containsString(expected));
        } finally {
            disconnect();
        }
    }

    @Test
    public void test_shouldStopAnApplication() {
        connect();
        createApplication();
        try {
            CommandResult result = getShell().executeCommand("stop");
            
            String expected = String.format("Your application %s is currently being stopped",
                    applicationName.toLowerCase());
            assertEquals(expected, result.getResult().toString());
        } finally {
            removeApplication();
            disconnect();
        }
    }

    @Test
    public void test_shouldStopAnApplicatioWithArgs() {
        connect();
        createApplication();
        disconnect();
        connect();
        try {
            CommandResult result = stopApplication();
            
            String expected = String.format("Your application %s is currently being stopped",
                    applicationName.toLowerCase());
            assertEquals(expected, result.getResult().toString());
        } finally {
            removeApplication();
            disconnect();
        }
    }

    @Test
    public void test_shouldNotStopAnApplicationBecauseUserIsNotLogged() {
        connect();
        createApplication();
        disconnect();
        try {
            CommandResult result = stopCurrentApplication();
            
            assertThat(result, isFailedCommand());
            String expected = "You are not connected to CloudUnit host! Please use connect command";
            assertThat(result.getException().getMessage(), containsString(expected));
        } finally {
            connect();
            removeApplication();
            disconnect();
        }
    }

    @Test
    public void test_shouldNotStopAnApplicationBecauseNoApplicationSelected() {
        connect();
        createApplication();
        disconnect();
        connect();
        try {
            CommandResult result = stopCurrentApplication();
            
            assertThat(result, isFailedCommand());
            String expected = "No application is currently selected by the following command line : use <application name>";
            assertThat(result.getException().getMessage(), containsString(expected));
        } finally {
            removeApplication();
            disconnect();
        }
    }

    @Test
    public void test_shouldNotStopAnApplicationBecauseApplicationDoesNotExist() {
        connect();
        String name = "qmozkdmqozkd";
        CommandResult result = stopApplication(name);
        
        assertThat(result, isFailedCommand());
        assertThat(result.getException().getMessage(), containsString("This application does not exist on this account"));
    }

    @Test
    public void test_shouldStartAnApplication() {
        connect();
        createApplication();
        stopCurrentApplication();
        try {
            CommandResult result = startCurrentApplication();
            
            String expected = String.format("Your application %s is currently being started",
                    applicationName.toLowerCase());
            assertEquals(expected, result.getResult().toString());
        } finally {
            removeApplication();
            disconnect();
        }
    }

    @Test
    public void test_shouldStartAnApplicatioWithArgs() {
        connect();
        createApplication();
        stopCurrentApplication();
        try {
            CommandResult result = startApplication();
            
            String expected = String.format("Your application %s is currently being started",
                    applicationName.toLowerCase());
            assertEquals(expected, result.getResult().toString());
        } finally {
            disconnect();
        }
    }

    @Test
    public void test_shouldNotStartAnApplicationBecauseUserIsNotLogged() {
        CommandResult result = startCurrentApplication();
        
        assertThat(result, isFailedCommand());
        String expected = "You are not connected to CloudUnit host! Please use connect command";
        assertThat(result.getException().getMessage(), containsString(expected));
    }


    @Test
    public void test_shouldNotStartAnApplicationBecauseNoApplicationSelected() {
        connect();
        try {
            CommandResult result = startCurrentApplication();
            
            assertThat(result, isFailedCommand());
            String expected = "No application is currently selected by the following command line : use <application name>";
            assertThat(result.getException().getMessage(), containsString(expected));
        } finally {
            disconnect();
        }
    }

    @Test
    public void test_shouldNotStartAnApplicationBecauseApplicationDoesNotExist() {
        connect();
        try {
            CommandResult result = getShell().executeCommand("start --name " + applicationName + "shadow");
            
            assertThat(result, isFailedCommand());
            String expected = "This application does not exist on this account";
            assertThat(result.getException().getMessage(), containsString(expected));
        } finally {
            disconnect();
        }
    }

    @Test
    public void test_shouldListApplications() {
        connect();
        createApplication();
        try {
            CommandResult result = listApplications();
            
            assertThat(result.getResult().toString(), containsString("found"));
        } finally {
            removeApplication();
            disconnect();
        }
    }

    @Test
    public void test_shouldNotListApplicationsBecauseUserIsNotLogged() {
        CommandResult result = listApplications();
        
        assertThat(result, isFailedCommand());
        assertThat(result.getException().getMessage(), containsString("You are not connected"));
    }

    @Test
    public void test_shouldDisplayApplicationInformation() {
        connect();
        createApplication();
        try {
            CommandResult result = information();
            
            assertEquals("Terminated", result.getResult().toString());
        } finally {
            removeApplication();
            disconnect();
        }
    }

    @Test
    public void test_shouldNotDisplayApplicationInformationBecauseUserIsNotLogged() {
        CommandResult result = information();
        
        assertThat(result, isFailedCommand());
        assertThat(result.getException().getMessage(), containsString("You are not connected"));
    }

    @Test
    public void test_shouldNotDisplayApplicationInformationBecauseNoApplicationSelected() {
        connect();
        try {
            CommandResult result = information();
            
            assertThat(result, isFailedCommand());
            String expected = "No application is currently selected";
            assertThat(result.getException().getMessage(), containsString(expected));
        } finally {
            disconnect();
        }
    }

    @Test
    public void test_shouldRemoveApplicationWithArgs() {
        connect();
        createApplication();
        disconnect();
        connect();
        try {
            CommandResult result = removeApplication();
            
            String expected = String.format("Your application %s is currently being removed",
                    applicationName.toLowerCase());
            assertThat(result.getResult().toString(), containsString(expected));
        } finally {
            disconnect();
        }
    }

    @Test
    public void test_shouldRemoveApplication() {
        connect();
        createApplication();
        try {
            CommandResult result = removeCurrentApplication();
            
            String expected = String.format("Your application %s is currently being removed",
                    applicationName.toLowerCase());
            assertEquals(expected, result.getResult().toString());
        } finally {
            disconnect();
        }
    }


    @Test
    public void test_shouldNotRemoveApplicationsBecauseUserIsNotLogged() {
        CommandResult result = removeCurrentApplication();
        
        assertThat(result, isFailedCommand());
        String expected = "You are not connected";
        assertThat(result.getException().getMessage(), containsString(expected));
    }

    @Test
    public void test_shouldNotRemoveApplicationBecauseNoApplicationSelected() {
        connect();
        try {
            CommandResult result = removeCurrentApplication();
            
            assertThat(result, isFailedCommand());
            assertThat(result.getException().getMessage(), containsString("No application is currently selected"));
        } finally {
            disconnect();
        }
    }


    @Test
    public void test_shouldNotRemoveApplicationWithArgsBecauseUserIsNotLogged() {
        CommandResult result = removeApplication();
        
        assertThat(result, isFailedCommand());
        assertThat(result.getException().getMessage(), containsString("You are not connected"));
    }

    @Test
    public void test_shouldNotRemoveApplicationWithArgsBecauseItDoesNotExist() {
        connect();
        try {
            CommandResult result = removeApplication("dzqmodzq");
            
            assertThat(result, isFailedCommand());
            assertThat(result.getException().getMessage(),
                    containsString("This application does not exist on this account"));
        } finally {
            disconnect();
        }
    }

    @Test
    public void test_shouldListContainers() {
        connect();
        createApplication();
        try {
            CommandResult result = listContainers();
            
            assertThat(result.getResult().toString(), containsString("found"));
        } finally {
            removeApplication();
            disconnect();
        }
    }
}
