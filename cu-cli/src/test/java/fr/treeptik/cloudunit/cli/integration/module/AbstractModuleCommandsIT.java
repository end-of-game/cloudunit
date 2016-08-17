package fr.treeptik.cloudunit.cli.integration.module;

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
public abstract class AbstractModuleCommandsIT extends AbstractShellIntegrationTest {

    private static String applicationName;
    protected String serverType;

    @BeforeClass
    public static void generateApplication() {
        applicationName = "App" + new Random().nextInt(10000);
    }

    @Test
    public void test00_shoudAddMysqlModule() {
        addModule("mysql-5-5");
    }

    @Test
    public void test01_shoudAddPostGresModule() {
        addModule("postgresql-9-3");
    }

    @Test
    public void test02_shoudAddMongoModule() {
        addModule("mongo-2-6");
    }

    @Test
    public void test03_shouldNotAddModuleBecauseImageDoesNotExist() {
        CommandResult cr = getShell().executeCommand("connect --login johndoe --password abc2015");
        cr = getShell().executeCommand("create-app --name " + applicationName + " --type " + serverType);
        cr = getShell().executeCommand("use " + applicationName);
        cr = getShell().executeCommand("add-module --name monmodule");
        String result = cr.getResult().toString();
        String expectedResult = "this module does not exist";
        Assert.assertEquals(expectedResult, result);
        cr = getShell().executeCommand("rm-app --name " + applicationName + " --scriptUsage");
        result = cr.getResult().toString();
        expectedResult = "Your application " + applicationName.toLowerCase() + " is currently being removed";
        Assert.assertEquals(expectedResult, result);
    }

    @Test
    public void test04_shouldNotAddModuleBecauseApplicationNotSelected() {
        CommandResult cr = getShell().executeCommand("connect --login johndoe --password abc2015");
        cr = getShell().executeCommand("add-module --name mysql-5-5");
        String result = cr.getResult().toString();
        String expectedResult = "No application is currently selected by the following command line : use <application name>";
        Assert.assertTrue(result.contains(expectedResult));
    }

    @Test
    public void test05_shouldNotAddModuleBecauseUserIsNotLogged() {
        CommandResult cr = getShell().executeCommand("create-app --name " + applicationName + " --type " + serverType);
        cr = getShell().executeCommand("use " + applicationName);
        cr = getShell().executeCommand("add-module --name mysql-5-5");
        String result = cr.getResult().toString();
        String expectedResult = "You are not connected to CloudUnit host! Please use connect command";
        Assert.assertTrue(result.contains(expectedResult));

    }


    @Test
    public void test10_shoudAddAndRemoveMysqlModule() {
        addAndRemoveModule("mysql-5-5");
    }

    @Test
    public void test11_shoudAddAndRemovePostGresModule() {
        addAndRemoveModule("postgresql-9-3");
    }

    @Test
    public void test12_shoudAddAndRemoveMongoModule() {
        addAndRemoveModule("mongo-2-6");
    }

    @Test
    public void test20_shouldListModules() {
        CommandResult cr = getShell().executeCommand("connect --login johndoe --password abc2015");
        cr = getShell().executeCommand("create-app --name " + applicationName + " --type " + serverType);
        cr = getShell().executeCommand("use " + applicationName);
        cr = getShell().executeCommand("add-module --name mysql-5-5");
        String result = cr.getResult().toString();
        String expectedResult = "Your module mysql-5-5 is currently being added to your application " + applicationName.toLowerCase();
        Assert.assertEquals(expectedResult, result);

        cr = getShell().executeCommand("display-modules");
        result = cr.getResult().toString();
        expectedResult = "1 modules found";
        Assert.assertEquals(expectedResult, result);


        cr = getShell().executeCommand("rm-app --name " + applicationName + " --scriptUsage");
        result = cr.getResult().toString();
        expectedResult = "Your application " + applicationName.toLowerCase() + " is currently being removed";
        Assert.assertEquals(expectedResult, result);
    }


    @Test
    public void test21_shouldNotListModulesBecauseApplicationNotSelected() {
        CommandResult cr = getShell().executeCommand("connect --login johndoe --password abc2015");
        cr = getShell().executeCommand("display-modules");
        String result = cr.getResult().toString();
        String expectedResult = "No application is currently selected by the following command line : use <application name>";
        Assert.assertTrue(result.contains(expectedResult));

    }

    @Test
    public void test22_shouldNotListModulesBecauseUserIsNotLogged() {
        CommandResult cr = getShell().executeCommand("display-modules");
        String result = cr.getResult().toString();
        String expectedResult = "You are not connected to CloudUnit host! Please use connect command";
        Assert.assertTrue(result.contains(expectedResult));
    }


    private void addModule(String moduleName) {
        CommandResult cr = getShell().executeCommand("connect --login johndoe --password abc2015");
        cr = getShell().executeCommand("create-app --name " + applicationName + " --type " + serverType);
        cr = getShell().executeCommand("use " + applicationName);
        cr = getShell().executeCommand("add-module --name " + moduleName);
        String result = cr.getResult().toString();
        String expectedResult = "Your module " + moduleName + " is currently being added to your application " + applicationName.toLowerCase();
        Assert.assertEquals(expectedResult, result);
        cr = getShell().executeCommand("rm-app --name " + applicationName + " --scriptUsage");
        result = cr.getResult().toString();
        expectedResult = "Your application " + applicationName.toLowerCase() + " is currently being removed";
        Assert.assertEquals(expectedResult, result);

    }

    private void addAndRemoveModule(String moduleName) {
        CommandResult cr = getShell().executeCommand("connect --login johndoe --password abc2015");
        cr = getShell().executeCommand("create-app --name " + applicationName + " --type " + serverType);
        cr = getShell().executeCommand("use " + applicationName);
        cr = getShell().executeCommand("add-module --name " + moduleName);
        String result = cr.getResult().toString();
        String expectedResult = "Your module " + moduleName + " is currently being added to your application " + applicationName.toLowerCase();
        Assert.assertEquals(expectedResult, result);
        cr = getShell().executeCommand("rm-module --name " + moduleName + "-1");
        result = cr.getResult().toString();
        expectedResult = "Your module " + moduleName + "-1"
                + " is currently being removed from your application " + applicationName.toLowerCase();
        Assert.assertEquals(expectedResult, result);
        cr = getShell().executeCommand("rm-app --name " + applicationName + " --scriptUsage");
        result = cr.getResult().toString();
        expectedResult = "Your application " + applicationName.toLowerCase() + " is currently being removed";
        Assert.assertEquals(expectedResult, result);
    }


}
