package fr.treeptik.cloudunit.cli.integration.application;

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
public abstract class AbstractApplicationCommandsIT extends AbstractShellIntegrationTest {

    private static String applicationName;
    protected String serverType;

    @BeforeClass
    public static void generateApplicationName() {
        applicationName = "App" + new Random().nextInt(10000);
    }

    @Test
    public void test00_shouldCreateApplication() {
        CommandResult cr = getShell().executeCommand("connect --login johndoe --password abc2015");
        cr = getShell().executeCommand("create-app --name " + applicationName + " --type " + serverType);
        String result = cr.getResult().toString();
        String expectedResult = "Your application " + applicationName + " is currently being installed";
        Assert.assertEquals(expectedResult, result);
    }

    @Test
    public void test01_shouldNotCreateApplicationBecauseUserIsNotLogged() {
        CommandResult cr = getShell().executeCommand("create-app --name " + applicationName + " --type myserver");
        String result = cr.getResult().toString();
        String expectedResult = "You are not connected to CloudUnit host! Please use connect command";
        Assert.assertTrue(result.contains(expectedResult));
    }

    @Test
    public void test02_shouldNotCreateApplicationBecauseNameAlreadyInUse() {
        CommandResult cr = getShell().executeCommand("connect --login johndoe --password abc2015");
        cr = getShell().executeCommand("create-app --name " + applicationName + " --type " + serverType);
        String result = cr.getResult().toString();
        String expectedResult = "This application name already exists";
        Assert.assertTrue(result.contains(expectedResult));
    }

    @Test
    public void test03_shouldNotCreateApplicationBecauseServerDoesNotExists() {
        CommandResult cr = getShell().executeCommand("connect --login johndoe --password abc2015");
        cr = getShell().executeCommand("create-app --name " + applicationName + " --type myserver");
        String result = cr.getResult().toString();
        String expectedResult = "This server image does not exist";
        Assert.assertTrue(result.contains(expectedResult));
    }

    @Test
    public void test04_shouldNotCreateApplicationNonAlphaNumericCharsBecauseApplicationAlreadyExists() {
        CommandResult cr = getShell().executeCommand("connect --login johndoe --password abc2015");
        cr = getShell().executeCommand("create-app --name " + applicationName + "&~~ --type " + serverType);
        String result = cr.getResult().toString();
        String expectedResult = "This application name already exists";
        Assert.assertTrue(result.contains(expectedResult));
    }

    @Test
    public void test10_shouldSelectAnApplication() {
        CommandResult cr = getShell().executeCommand("connect --login johndoe --password abc2015");
        cr = getShell().executeCommand("use " + applicationName);
        String result = cr.getResult().toString();
        String expectedResult = "Current application : " + applicationName.toLowerCase();
        Assert.assertEquals(expectedResult, result);
    }

    @Test
    public void test11_shouldNotSelectAnApplicationBecauseUserIsNotLogged() {
        CommandResult cr = getShell().executeCommand("use " + applicationName);
        String result = cr.getResult().toString();
        String expectedResult = "You are not connected to CloudUnit host! Please use connect command";
        Assert.assertTrue(result.contains(expectedResult));
    }

    @Test
    public void test12_shouldNotSelectAnApplicationBecauseDoesNotExist() {
        CommandResult cr = getShell().executeCommand("connect --login johndoe --password abc2015");
        cr = getShell().executeCommand("use " + applicationName + "shadow");
        String result = cr.getResult().toString();
        String expectedResult = "This application does not exist on this account";
        Assert.assertTrue(result.contains(expectedResult));
    }

    @Test
    public void test20_shouldStopAnApplication() {
        CommandResult cr = getShell().executeCommand("connect --login johndoe --password abc2015");
        cr = getShell().executeCommand("use " + applicationName);
        cr = getShell().executeCommand("stop");
        String result = cr.getResult().toString();
        String expectedResult = "Your application " + applicationName.toLowerCase() + " is currently being stopped";
        Assert.assertEquals(expectedResult, result);
    }

    @Test
    public void test20bis_shouldStopAnApplicatioWithArgs() {
        CommandResult cr = getShell().executeCommand("connect --login johndoe --password abc2015");
        cr = getShell().executeCommand("stop --name " + applicationName);
        String result = cr.getResult().toString();
        String expectedResult = "Your application " + applicationName.toLowerCase() + " is currently being stopped";
        Assert.assertEquals(expectedResult, result);
    }

    @Test
    public void test21_shouldNotStopAnApplicationBecauseUserIsNotLogged() {
        CommandResult cr = getShell().executeCommand("use " + applicationName);
        cr = getShell().executeCommand("stop");
        String result = cr.getResult().toString();
        String expectedResult = "You are not connected to CloudUnit host! Please use connect command";
        Assert.assertTrue(result.contains(expectedResult));
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

}
