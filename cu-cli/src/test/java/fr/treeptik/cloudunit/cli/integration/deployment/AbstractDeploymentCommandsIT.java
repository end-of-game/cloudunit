package fr.treeptik.cloudunit.cli.integration.deployment;

import static fr.treeptik.cloudunit.cli.integration.ShellMatchers.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.junit.Test;
import org.springframework.shell.core.CommandResult;

import fr.treeptik.cloudunit.cli.integration.AbstractShellIntegrationTest;

/**
 * Created by guillaume on 16/10/15.
 */
public abstract class AbstractDeploymentCommandsIT extends AbstractShellIntegrationTest {
    private static final String WAR_FILENAME = "src/test/resources/helloworld.war";

    protected AbstractDeploymentCommandsIT(String serverType) {
        super(serverType);
    }

	@Test
	public void test_shouldDeployAHelloworldApp() {
	    connect();
	    createApplication();
	    try {
    		CommandResult result = deployWar();
    		
    		assertThat(result, isSuccessfulCommand());
    		assertThat(result.getResult().toString(), containsString("deployed"));
	    } finally {
            removeApplication();
            disconnect();
        }
	}

	@Test
	public void test_shouldNotDeployBecauseWarIsNotFound() {
	    connect();
        createApplication();
        try {
            CommandResult result = deployWar("dzqmok.war");
            
            assertThat(result, isFailedCommand());
        } finally {
            removeApplication();
            disconnect();
        }
	}

	@Test
	public void test_shouldNotDeployBecauseApplicationNotSelected() {
        connect();
        try {
            CommandResult result = deployWar();
            
            assertThat(result, isFailedCommand());
            assertThat(result.getException().getMessage(), containsString("not selected"));
        } finally {
            disconnect();
        }
	}

	@Test
	public void test_shouldNotDeployBecauseUserIsNotLogged() {
	    CommandResult result = deployWar();
        
        assertThat(result, isFailedCommand());
        assertThat(result.getException().getMessage(), containsString("not connected"));
	}
	
	private CommandResult deployWar() {
	    return deployWar(WAR_FILENAME);
	}
	
	private CommandResult deployWar(String path) {
	    return getShell().executeCommand(String.format("deploy --path %s --openBrowser false", path));
	}
}
