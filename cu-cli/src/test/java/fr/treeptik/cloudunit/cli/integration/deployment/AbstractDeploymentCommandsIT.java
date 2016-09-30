package fr.treeptik.cloudunit.cli.integration.deployment;

import static org.hamcrest.Matchers.*;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.shell.core.CommandResult;

import fr.treeptik.cloudunit.cli.integration.AbstractShellIntegrationTest;

/**
 * Created by guillaume on 16/10/15.
 */
public abstract class AbstractDeploymentCommandsIT extends AbstractShellIntegrationTest {
    private static final String WAR_FILENAME = "helloworld.war";

    protected AbstractDeploymentCommandsIT(String serverType) {
        super(serverType);
    }

	@Test
	public void test00_shouldDeployAHelloworldApp() {
		CommandResult cr = getShell().executeCommand("connect --login johndoe --password abc2015");
		cr = getShell().executeCommand("create-app --name " + applicationName + " --type " + serverType);
		cr = getShell().executeCommand("use " + applicationName);
		cr = getShell().executeCommand(
				"! wget -P /tmp/ https://github.com/Treeptik/CloudUnit/releases/download/1.0/" + WAR_FILENAME);
		cr = getShell().executeCommand("deploy --path /tmp/" + WAR_FILENAME);
		String result = cr.getResult().toString();
		String expectedResult = "War deployed - Access on http://" + applicationName.toLowerCase()
				+ "-johndoe-admin.cloudunit.dev";
		Assert.assertEquals(expectedResult, result);

	}

	@Test
	public void test01_shouldNotDeployBecauseWarIsNotFound() {
		CommandResult cr = getShell().executeCommand("connect --login johndoe --password abc2015");
		cr = getShell().executeCommand("use " + applicationName);
		cr = getShell().executeCommand("deploy --path /tmp/" + WAR_FILENAME + "shadow");
		String result = cr.getResult().toString();
		String expectedResult = "Check your syntax and option chosen and it's the right path";
		Assert.assertEquals(expectedResult, result);

	}

	@Test
	public void test02_shouldNotDeployBecauseApplicationNotSelected() {
		CommandResult cr = getShell().executeCommand("connect --login johndoe --password abc2015");
		cr = getShell().executeCommand("deploy --path /tmp/" + WAR_FILENAME);
		String result = cr.getResult().toString();
		String expectedResult = "No application is currently selected by the following command line : use <application name>";
		Assert.assertThat(result, containsString(expectedResult));

	}

	@Test
	public void test03_shouldNotDeployBecauseUserIsNotLogged() {
		CommandResult cr = getShell().executeCommand("use " + applicationName);
		cr = getShell().executeCommand("use " + applicationName);
		cr = getShell().executeCommand("deploy --path /tmp/" + WAR_FILENAME);
		String result = cr.getResult().toString();
		String expectedResult = "You are not connected to CloudUnit host! Please use connect command";
		Assert.assertThat(result, containsString(expectedResult));

	}

	@Test
	public void test90_cleanEnv() {
		CommandResult cr = getShell().executeCommand("connect --login johndoe --password abc2015");
		cr = getShell().executeCommand("rm-app --name " + applicationName + " --scriptUsage");
		String result = cr.getResult().toString();
		String expectedResult = "Your application " + applicationName.toLowerCase() + " is currently being removed";
		Assert.assertEquals(expectedResult, result);
	}

}
