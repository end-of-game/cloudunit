package fr.treeptik.cloudunit.cli.integration.environmentVariables;

import fr.treeptik.cloudunit.cli.integration.AbstractShellIntegrationTest;
import org.junit.*;
import org.junit.runners.MethodSorters;
import org.springframework.shell.core.CommandResult;

import java.util.Random;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AbstractEnvironmentVariablesCommandsIT extends AbstractShellIntegrationTest {

    private static String applicationName;
    protected String serverType;

    @Before
    public void generateApplication() {
        applicationName = "App" + new Random().nextInt(10000);
    }

    @After
    public void removeApplication() {
        CommandResult cr = getShell().executeCommand("connect --login johndoe --password abc2015");
        cr = getShell().executeCommand("use " + applicationName);
        cr = getShell().executeCommand("rm-app --scriptUsage true");
    }

    @Test
    public void test00_shouldCreateEnvironmentVariable() {
        CommandResult cr = getShell().executeCommand("connect --login johndoe --password abc2015");
        cr = getShell().executeCommand("create-app --name " + applicationName + " --type " + serverType);
        cr = getShell().executeCommand("use " + applicationName);
        cr = getShell().executeCommand("create-var-env --key key --value value");
        String result = cr.getResult().toString();
        String expectedResult = "An environment variable has been successfully added to " + applicationName.toLowerCase();
        Assert.assertEquals(expectedResult, result);
    }

    @Test(expected = NullPointerException.class)
    public void test01_shouldNotCreateEnvironmentVariableEmptyKey() {
        CommandResult cr = getShell().executeCommand("connect --login johndoe --password abc2015");
        cr = getShell().executeCommand("create-app --name " + applicationName + " --type " + serverType);
        cr = getShell().executeCommand("use " + applicationName);
        cr = getShell().executeCommand("create-var-env --key --value value");
        String result = cr.getResult().toString();
    }

    @Test
    public void test02_shouldNotCreateEnvironmentVariableForbiddenKey() {
        CommandResult cr = getShell().executeCommand("connect --login johndoe --password abc2015");
        cr = getShell().executeCommand("create-app --name " + applicationName + " --type " + serverType);
        cr = getShell().executeCommand("use " + applicationName);
        cr = getShell().executeCommand("create-var-env --key azérty --value value");
        String result = cr.getResult().toString();
        String expectedResult = "An environment variable has been successfully added to " + applicationName.toLowerCase();
        Assert.assertNotEquals(expectedResult, result);
    }

    @Test
    public void test10_shouldRemoveEnvironmentVariable() {
        CommandResult cr = getShell().executeCommand("connect --login johndoe --password abc2015");
        cr = getShell().executeCommand("create-app --name " + applicationName + " --type " + serverType);
        cr = getShell().executeCommand("use " + applicationName);
        cr = getShell().executeCommand("create-var-env --key key --value value");
        cr = getShell().executeCommand("rm-var-env --key key");
        String result = cr.getResult().toString();
        String expectedResult = "This environment variable has successful been deleted";
        Assert.assertEquals(expectedResult, result);
    }

    @Test(expected = NullPointerException.class)
    public void test11_shouldNotRemoveEnvironmentVariableEmptyKey() {
        CommandResult cr = getShell().executeCommand("connect --login johndoe --password abc2015");
        cr = getShell().executeCommand("create-app --name " + applicationName + " --type " + serverType);
        cr = getShell().executeCommand("use " + applicationName);
        cr = getShell().executeCommand("create-var-env --key key --value value");
        cr = getShell().executeCommand("rm-var-env --key");
        String result = cr.getResult().toString();
    }

    @Test
    public void test12_shouldNotRemoveEnvironmentVariableUnexistingKey() {
        CommandResult cr = getShell().executeCommand("connect --login johndoe --password abc2015");
        cr = getShell().executeCommand("create-app --name " + applicationName + " --type " + serverType);
        cr = getShell().executeCommand("use " + applicationName);
        cr = getShell().executeCommand("rm-var-env --key azerty");
        String result = cr.getResult().toString();
        String expectedResult = "An environment variable has been successfully added to " + applicationName.toLowerCase();
        Assert.assertNotEquals(expectedResult, result);
    }

    @Test
    public void test20_shouldListEnvironmentVariables() {
        CommandResult cr = getShell().executeCommand("connect --login johndoe --password abc2015");
        cr = getShell().executeCommand("create-app --name " + applicationName + " --type " + serverType);
        cr = getShell().executeCommand("use " + applicationName);
        cr = getShell().executeCommand("create-var-env --key key --value value");
        cr = getShell().executeCommand("list-var-env");
        String result = cr.getResult().toString();
        String expectedResult = " 1 variables found!";
        Assert.assertNotEquals(expectedResult, result);
    }

    @Test
    public void test30_shouldUpdateEnvironmentVariable() {
        CommandResult cr = getShell().executeCommand("connect --login johndoe --password abc2015");
        cr = getShell().executeCommand("create-app --name " + applicationName + " --type " + serverType);
        cr = getShell().executeCommand("use " + applicationName);
        cr = getShell().executeCommand("create-var-env --key key --value value");
        cr = getShell().executeCommand("update-var-env --old-key key --new-key keyUpdated --value value");
        String result = cr.getResult().toString();
        String expectedResult = "This environment variable has successful been updated";
        Assert.assertEquals(expectedResult, result);
    }

    @Test(expected = NullPointerException.class)
    public void test31_shouldNotUpdateEnvironmentVariableEmptyOldKey() {
        CommandResult cr = getShell().executeCommand("connect --login johndoe --password abc2015");
        cr = getShell().executeCommand("create-app --name " + applicationName + " --type " + serverType);
        cr = getShell().executeCommand("use " + applicationName);
        cr = getShell().executeCommand("create-var-env --key key --value value");
        cr = getShell().executeCommand("update-var-env --old-key --new-key key --value value");
        String result = cr.getResult().toString();
    }

    @Test(expected = NullPointerException.class)
    public void test32_shouldNotUpdateEnvironmentVariableEmptyNewKey() {
        CommandResult cr = getShell().executeCommand("connect --login johndoe --password abc2015");
        cr = getShell().executeCommand("create-app --name " + applicationName + " --type " + serverType);
        cr = getShell().executeCommand("use " + applicationName);
        cr = getShell().executeCommand("create-var-env --key key --value value");
        cr = getShell().executeCommand("update-var-env --old-key key --new-key --value value");
        String result = cr.getResult().toString();
    }

    @Test
    public void test33_shouldNotUpdateEnvironmentVariableUnexistingOldKey() {
        CommandResult cr = getShell().executeCommand("connect --login johndoe --password abc2015");
        cr = getShell().executeCommand("create-app --name " + applicationName + " --type " + serverType);
        cr = getShell().executeCommand("use " + applicationName);
        cr = getShell().executeCommand("create-var-env --key key --value value");
        cr = getShell().executeCommand("update-var-env --old-key key2 --new-key key --value value");
        String result = cr.getResult().toString();
        String expectedResult = "An environment variable has been successfully added to " + applicationName.toLowerCase();
        Assert.assertNotEquals(expectedResult, result);
    }

}
