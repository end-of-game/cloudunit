package fr.treeptik.cloudunit.cli.integration.application;

import fr.treeptik.cloudunit.cli.integration.AbstractShellIntegrationTest;
import org.junit.*;
import org.junit.runners.MethodSorters;
import org.springframework.shell.core.CommandResult;

import java.util.Random;

/**
 * Created by guillaume on 16/10/15.
 */
public abstract class AbstractApplicationCommandsIT extends AbstractShellIntegrationTest {

    private static String applicationName;
    protected String serverType;
    private CommandResult cr;

    @BeforeClass
    public static void generateApplicationName() {
        applicationName = "app" + new Random().nextInt(10000);
    }

    @Test
    public void test_shouldCreateApplication() {
        connect();
        createApplication(applicationName, serverType);
        deleteApplication();
        disconnect();
    }

    @Test
    public void test_shouldNotCreateApplicationBecauseUserIsNotLogged() {
        createApplication(applicationName, serverType);
        String result = cr.getResult().toString();
        String expectedResult = "You are not connected to CloudUnit host! Please use connect command";
        Assert.assertTrue(result.contains(expectedResult));
        disconnect();
    }

    @Test
    public void test_shouldBeIdentical() {
        connect();
        String name = "backendmaster" + new Random().nextInt(1000);
        createApplication(name, serverType);
        String result = cr.getResult().toString();
        String expectedResult = "Your application "+name+" is currently being installed";
        Assert.assertTrue(result.contains(expectedResult));
        deleteApplication(name);
        disconnect();
    }

    @Test
    public void test02_shouldNotCreateApplicationBecauseNameAlreadyInUse() {
        connect();
        createApplication(applicationName, serverType);
        createApplication(applicationName, serverType);
        String result = cr.getResult().toString();
        String expectedResult = "This application name already exists";
        Assert.assertTrue(result.contains(expectedResult));
        deleteApplication();
        disconnect();
    }

    @Test
    public void test03_shouldNotCreateApplicationBecauseServerDoesNotExists() {
        connect();
        createApplication(applicationName, "xxx");
        String result = cr.getResult().toString();
        String expectedResult = "This server image does not exist";
        Assert.assertTrue(result.contains(expectedResult));
        deleteApplication();
        disconnect();
    }

    @Test
    public void test04_shouldNotCreateApplicationNonAlphaNumericCharsBecauseApplicationAlreadyExists() {
        connect();
        createApplication(applicationName, serverType);
        createApplication(applicationName+"&~~", serverType);
        String result = cr.getResult().toString();
        String expectedResult = "This application name already exists";
        Assert.assertTrue(result.contains(expectedResult));
        deleteApplication();
        disconnect();
    }

    @Test
    public void test10_shouldSelectAnApplication() {
        connect();
        createApplication(applicationName, serverType);
        useApplication(applicationName);
        String result = cr.getResult().toString();
        String expectedResult = "Current application : " + applicationName.toLowerCase();
        Assert.assertEquals(expectedResult, result);
        deleteApplication();
        disconnect();
    }

    @Test
    public void test11_shouldNotSelectAnApplicationBecauseUserIsNotLogged() {
        useApplication(applicationName);
        String result = cr.getResult().toString();
        String expectedResult = "You are not connected to CloudUnit host! Please use connect command";
        Assert.assertTrue(result.contains(expectedResult));
        disconnect();
    }

    @Test
    public void test12_shouldNotSelectAnApplicationBecauseDoesNotExist() {
        connect();
        useApplication("shadowApplication");
        String result = cr.getResult().toString();
        String expectedResult = "This application does not exist on this account";
        Assert.assertTrue(result.contains(expectedResult));
        disconnect();
    }

    @Test
    public void test20_shouldStopAnApplication() {
        connect();
        createApplication(applicationName, serverType);
        useApplication(applicationName);
        cr = getShell().executeCommand("stop");
        String result = cr.getResult().toString();
        String expectedResult = "Your application " + applicationName.toLowerCase() + " is currently being stopped";
        Assert.assertEquals(expectedResult, result);
        deleteApplication();
        disconnect();
    }

    @Test
    public void test20bis_shouldStopAnApplicatioWithArgs() {
        connect();
        createApplication(applicationName, serverType);
        cr = getShell().executeCommand("stop --name " + applicationName);
        String result = cr.getResult().toString();
        String expectedResult = "Your application " + applicationName.toLowerCase() + " is currently being stopped";
        Assert.assertEquals(expectedResult, result);
        deleteApplication();
        disconnect();
    }

    @Test
    public void test21_shouldNotStopAnApplicationBecauseUserIsNotLogged() {
        connect();
        createApplication(applicationName, serverType);
        disconnect();
        cr = getShell().executeCommand("stop");
        String result = cr.getResult().toString();
        String expectedResult = "You are not connected to CloudUnit host! Please use connect command";
        Assert.assertTrue(result.contains(expectedResult));
        connect();
        useApplication(applicationName);
        deleteApplication();
        disconnect();
    }

    @Test
    public void test22_shouldNotStopAnApplicationBecauseNoApplicationSelected() {
        CommandResult cr = getShell().executeCommand("connect --login johndoe --password abc2015");
        cr = getShell().executeCommand("stop");
        String result = cr.getResult().toString();
        String expectedResult = "No application is currently selected by the following command line : use <application name>";
        Assert.assertTrue(result.contains(expectedResult));
    }

    @Test
    public void test23_shouldNotStopAnApplicationBecauseApplicationDoesNotExist() {
        CommandResult cr = getShell().executeCommand("connect --login johndoe --password abc2015");
        cr = getShell().executeCommand("stop --name " + applicationName + "shadow");
        String result = cr.getResult().toString();
        String expectedResult = "This application does not exist on this account";
        Assert.assertTrue(result.contains(expectedResult));
    }

    @Test
    public void test30_shouldStartAnApplication() {
        CommandResult cr = getShell().executeCommand("connect --login johndoe --password abc2015");
        cr = getShell().executeCommand("use " + applicationName);
        cr = getShell().executeCommand("start");
        String result = cr.getResult().toString();
        String expectedResult = "Your application " + applicationName.toLowerCase() + " is currently being started";
        Assert.assertEquals(expectedResult, result);
    }

    @Test
    public void test30bis_shouldStartAnApplicatioWithArgs() {
        CommandResult cr = getShell().executeCommand("connect --login johndoe --password abc2015");
        cr = getShell().executeCommand("start --name " + applicationName);
        String result = cr.getResult().toString();
        String expectedResult = "Your application " + applicationName.toLowerCase() + " is currently being started";
        Assert.assertEquals(expectedResult, result);
    }

    @Test
    public void test31_shouldNotStartAnApplicationBecauseUserIsNotLogged() {
        CommandResult cr = getShell().executeCommand("use " + applicationName);
        cr = getShell().executeCommand("start");
        String result = cr.getResult().toString();
        String expectedResult = "You are not connected to CloudUnit host! Please use connect command";
        Assert.assertTrue(result.contains(expectedResult));
    }


    @Test
    public void test32_shouldNotStartAnApplicationBecauseNoApplicationSelected() {
        CommandResult cr = getShell().executeCommand("connect --login johndoe --password abc2015");
        cr = getShell().executeCommand("start");
        String result = cr.getResult().toString();
        String expectedResult = "No application is currently selected by the following command line : use <application name>";
        Assert.assertTrue(result.contains(expectedResult));
    }

    @Test
    public void test33_shouldNotStartAnApplicationBecauseApplicationDoesNotExist() {
        CommandResult cr = getShell().executeCommand("connect --login johndoe --password abc2015");
        cr = getShell().executeCommand("start --name " + applicationName + "shadow");
        String result = cr.getResult().toString();
        String expectedResult = "This application does not exist on this account";
        Assert.assertTrue(result.contains(expectedResult));
    }


    @Test
    public void test40_shouldListApplications() {
        CommandResult cr = getShell().executeCommand("connect --login johndoe --password abc2015");
        cr = getShell().executeCommand("list-apps");
        String result = cr.getResult().toString();
        String expectedResult = "found";
        Assert.assertTrue(result.contains(expectedResult));
    }

    @Test
    public void test41_shouldNotListApplicationsBecauseUserIsNotLogged() {
        CommandResult cr = getShell().executeCommand("list-apps");
        String result = cr.getResult().toString();
        String expectedResult = "You are not connected to CloudUnit host! Please use connect command";
        Assert.assertTrue(result.contains(expectedResult));
    }

    @Test
    public void test50_shouldDisplayApplicationInformations() {
        CommandResult cr = getShell().executeCommand("connect --login johndoe --password abc2015");
        cr = getShell().executeCommand("use " + applicationName);
        cr = getShell().executeCommand("informations");
        String result = cr.getResult().toString();
        String expectedResult = "Terminated";
        Assert.assertEquals(expectedResult, result);
    }

    @Test
    public void test51_shouldNotDisplayApplicationInformationsBecauseUserIsNotLogged() {
        CommandResult cr = getShell().executeCommand("use " + applicationName);
        cr = getShell().executeCommand("informations");
        String result = cr.getResult().toString();
        String expectedResult = "You are not connected to CloudUnit host! Please use connect command";
        Assert.assertTrue(result.contains(expectedResult));
    }

    @Test
    public void test52_shouldNotDisplayApplicationInformationsBecauseNoApplicationSelected() {
        CommandResult cr = getShell().executeCommand("connect --login johndoe --password abc2015");
        cr = getShell().executeCommand("informations");
        String result = cr.getResult().toString();
        String expectedResult = "No application is currently selected by the following command line : use <application name>";
        Assert.assertTrue(result.contains(expectedResult));
    }

    @Test
    public void test60_shouldRemoveApplicationWithArgs() {
        CommandResult cr = getShell().executeCommand("connect --login johndoe --password abc2015");
        cr = getShell().executeCommand("rm-app --name " + applicationName + " --scriptUsage");
        String result = cr.getResult().toString();
        String expectedResult = "Your application " + applicationName.toLowerCase() + " is currently being removed";
        Assert.assertTrue(result.contains(expectedResult));
    }

    @Test
    public void test61_shouldRemoveApplication() {
        CommandResult cr = getShell().executeCommand("connect --login johndoe --password abc2015");
        cr = getShell().executeCommand("create-app --name " + applicationName + " --type " + serverType);
        cr = getShell().executeCommand("use " + applicationName);
        cr = getShell().executeCommand("rm-app --scriptUsage");
        String result = cr.getResult().toString();
        String expectedResult = "Your application " + applicationName.toLowerCase() + " is currently being removed";
        Assert.assertEquals(expectedResult, result);
    }


    @Test
    public void test62_shouldNotRemoveApplicationsBecauseUserIsNotLogged() {
        CommandResult cr = getShell().executeCommand("use " + applicationName);
        cr = getShell().executeCommand("rm-app");
        String result = cr.getResult().toString();
        String expectedResult = "You are not connected to CloudUnit host! Please use connect command";
        Assert.assertTrue(result.contains(expectedResult));
    }

    @Test
    public void test63_shouldNotRemoveApplicationBecauseNoApplicationSelected() {
        CommandResult cr = getShell().executeCommand("connect --login johndoe --password abc2015");
        cr = getShell().executeCommand("rm-app");
        String result = cr.getResult().toString();
        String expectedResult = "No application is currently selected by the following command line : use <application name>";
        Assert.assertTrue(result.contains(expectedResult));
    }


    @Test
    public void test64_shouldNotRemoveApplicationWithArgsBecauseUserIsNotLogged() {
        CommandResult cr = getShell().executeCommand("rm-app --name " + applicationName + " --scriptUsage");
        String result = cr.getResult().toString();
        String expectedResult = "You are not connected to CloudUnit host! Please use connect command";
        Assert.assertTrue(result.contains(expectedResult));
    }

    @Test
    public void test65_shouldNotRemoveApplicationWithArgsBecauseItDoesNotExist() {
        CommandResult cr = getShell().executeCommand("connect --login johndoe --password abc2015");
        cr = getShell().executeCommand("rm-app --name " + applicationName + "shadow" + " --scriptUsage");
        String result = cr.getResult().toString();
        String expectedResult = "This application does not exist on this account";
        Assert.assertTrue(result.contains(expectedResult));
    }

    @Test
    public void test70_shouldListContainers() {
        CommandResult cr = getShell().executeCommand("connect --login johndoe --password abc2015");
        cr = getShell().executeCommand("create-app --name " + applicationName + " --type " + serverType);
        cr = getShell().executeCommand("list-containers --name " + applicationName);
        String result = cr.getResult().toString();
        String expectedResult = "found";
        Assert.assertTrue(result.contains(expectedResult));
    }

    private void connect() {
        cr = getShell().executeCommand("connect --login johndoe --password abc2015 ");
        Assert.assertTrue("Connect to application", cr.isSuccess());
    }

    private void disconnect() {
        cr = getShell().executeCommand("disconnect");
        Assert.assertTrue("Disconnect from application", cr.isSuccess());
    }

    private void useApplication(String name) {
        cr = getShell().executeCommand("use " + name);
        Assert.assertTrue("Use Application", cr.isSuccess());
    }

    private void createApplication(String name, String type) {
        cr = getShell().executeCommand("create-app --name " + name + " --type " + type);
        Assert.assertTrue("Create Application", cr.isSuccess());
    }

    private void deleteApplication() {
        cr = getShell().executeCommand("rm-app --scriptUsage");
        Assert.assertTrue("Delete application", cr.isSuccess());
    }

    private void deleteApplication(String app) {
        cr = getShell().executeCommand("rm-app " + app +  "--scriptUsage ");
        Assert.assertTrue("Delete application", cr.isSuccess());
    }


}
