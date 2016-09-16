package fr.treeptik.cloudunit.cli.integration.server;

import fr.treeptik.cloudunit.cli.integration.AbstractShellIntegrationTest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.shell.core.CommandResult;

import java.util.Random;

/**
 * Created by guillaume on 16/10/15.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public abstract class AbstractServerCommandsIT extends AbstractShellIntegrationTest {

    private static String applicationName;
    protected String serverType;

    @BeforeClass
    public static void generateApplication() {
        applicationName = "app" + new Random().nextInt(10000);
    }

    @Test
    public void test00_shouldChangeJavaVersion() {
        CommandResult cr = getShell().executeCommand("connect --login johndoe --password abc2015");
        cr = getShell().executeCommand("create-app --name " + applicationName + " --type " + serverType);
        cr = getShell().executeCommand("use " + applicationName);
        cr = getShell().executeCommand("change-java-version --javaVersion jdk1.8.0_25");
        String result = cr.getResult().toString();
        String expectedResult = "Your java version has been successfully changed";
        Assert.assertEquals(expectedResult, result);
    }

    @Test
    public void test01_shouldNotChangeJavaVersionBecauseApplicationNotSelected() {
        CommandResult cr = getShell().executeCommand("connect --login johndoe --password abc2015");
        cr = getShell().executeCommand("change-java-version --javaVersion jdk1.8.0_25");
        String result = cr.getResult().toString();
        String expectedResult = "No application is currently selected by the following command line : use <application name>";
        Assert.assertTrue(result.contains(expectedResult));
    }

    @Test
    public void test02_shouldNotChangeJavaVersionBecauseUserIsNotLogged() {
        CommandResult cr = getShell().executeCommand("use " + applicationName);
        cr = getShell().executeCommand("change-java-version --javaVersion jdk1.8.0_25");
        String result = cr.getResult().toString();
        String expectedResult = "You are not connected to CloudUnit host! Please use connect command";
        Assert.assertTrue(result.contains(expectedResult));

    }

    @Test
    public void test03_shouldNotChangeJavaVersionBecauseJDKValueDoesNotExist() {
        CommandResult cr = getShell().executeCommand("connect --login johndoe --password abc2015");
        cr = getShell().executeCommand("use " + applicationName);
        cr = getShell().executeCommand("change-java-version --javaVersion jdk1.XXX");
        String result = cr.getResult().toString();
        String expectedResult = "The specified java version is not available";
        Assert.assertTrue(result.contains(expectedResult));
    }

    @Test
    public void test10_shouldChangeMemory() {
        CommandResult cr = getShell().executeCommand("connect --login johndoe --password abc2015");
        cr = getShell().executeCommand("use " + applicationName);
        cr = getShell().executeCommand("change-jvm-memory --size 1024");
        String result = cr.getResult().toString();
        String expectedResult = "Change memory on " + applicationName.toLowerCase() + " successful";
        Assert.assertEquals(expectedResult, result);

    }

    @Test
    public void test11_shouldNotChangeMemoryBecauseApplicationNotSelected() {
        CommandResult cr = getShell().executeCommand("connect --login johndoe --password abc2015");
        cr = getShell().executeCommand("change-jvm-memory --size 1024");
        String result = cr.getResult().toString();
        String expectedResult = "No application is currently selected by the following command line : use <application name>";
        Assert.assertTrue(result.contains(expectedResult));
    }

    @Test
    public void test12_shouldNotChangeMemoryBecauseUserIsNotLogged() {
        CommandResult cr = getShell().executeCommand("use " + applicationName);
        cr = getShell().executeCommand("change-jvm-memory --size 1024");
        String result = cr.getResult().toString();
        String expectedResult = "You are not connected to CloudUnit host! Please use connect command";
        Assert.assertTrue(result.contains(expectedResult));

    }

    @Test
    public void test10_shouldNotChangeMemoryBecauseJVMSizeIsNotAuthorized() {
        CommandResult cr = getShell().executeCommand("connect --login johndoe --password abc2015");
        cr = getShell().executeCommand("use " + applicationName);
        cr = getShell().executeCommand("change-jvm-memory --size 128");
        String result = cr.getResult().toString();
        String expectedResult = "The memory value you have put is not authorized (512, 1024, 2048, 3072)";
        Assert.assertTrue(result.contains(expectedResult));
    }

    @Test
    public void test20_shouldChangeJavaOpts() {
        CommandResult cr = getShell().executeCommand("connect --login johndoe --password abc2015");
        cr = getShell().executeCommand("use " + applicationName);
        cr = getShell().executeCommand("add-jvm-option \"-Dkey=value\"");
        String result = cr.getResult().toString();
        String expectedResult = "Add java options to " + applicationName.toLowerCase() + " application successfully";
        Assert.assertEquals(expectedResult, result);

    }

    @Test
    public void test21_shouldNotChangeJavaOptsBecauseApplicationNotSelected() {
        CommandResult cr = getShell().executeCommand("connect --login johndoe --password abc2015");
        cr = getShell().executeCommand("add-jvm-option \"-Dkey=value\"");
        String result = cr.getResult().toString();
        String expectedResult = "No application is currently selected by the following command line : use <application name>";
        Assert.assertTrue(result.contains(expectedResult));
    }

    @Test
    public void test22_shouldNotChangeJavaOptsBecauseUserIsNotLogged() {
        CommandResult cr = getShell().executeCommand("use " + applicationName);
        cr = getShell().executeCommand("add-jvm-option \"-Dkey=value\"");
        String result = cr.getResult().toString();
        String expectedResult = "You are not connected to CloudUnit host! Please use connect command";
        Assert.assertTrue(result.contains(expectedResult));
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
