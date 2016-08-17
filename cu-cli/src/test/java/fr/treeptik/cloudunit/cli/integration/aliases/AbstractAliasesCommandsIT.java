package fr.treeptik.cloudunit.cli.integration.aliases;

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
public abstract class AbstractAliasesCommandsIT extends AbstractShellIntegrationTest {


    private static String applicationName;
    protected String serverType;
    private String alias = "myalias.cloudunit.dev";

    @BeforeClass
    public static void generateApplication() {
        applicationName = "App" + new Random().nextInt(10000);
    }

    @Test
    public void test00_shouldAddAnAlias() {
        CommandResult cr = getShell().executeCommand("connect --login johndoe --password abc2015");
        cr = getShell().executeCommand("create-app --name " + applicationName + " --type " + serverType);
        cr = getShell().executeCommand("use " + applicationName);
        cr = getShell().executeCommand("add-alias --alias " + alias);
        String result = cr.getResult().toString();
        String expectedResult = "An alias has been successfully added to " + applicationName.toLowerCase();
        Assert.assertEquals(expectedResult, result);
    }

    @Test
    public void test01_shoulAliasRemovingForbiddenExpression() {
        CommandResult cr = getShell().executeCommand("connect --login johndoe --password abc2015");
        cr = getShell().executeCommand("use " + applicationName);
        cr = getShell().executeCommand("add-alias --alias https://" + alias + ".eu");
        String result = cr.getResult().toString();
        String expectedResult = "An alias has been successfully added to " + applicationName.toLowerCase();
        Assert.assertEquals(expectedResult, result);
    }

    @Test
    public void test02_shouldNotAddAnAliasBecauseApplicationNotSelected() {
        CommandResult cr = getShell().executeCommand("connect --login johndoe --password abc2015");
        cr = getShell().executeCommand("add-alias --alias " + alias);
        String result = cr.getResult().toString();
        String expectedResult = "No application is currently selected by the following command line : use <application name>";
        Assert.assertTrue(result.contains(expectedResult));
    }

    @Test
    public void test03_shouldNotAddAnAliasBecauseUserIsNotLogged() {
        CommandResult cr = getShell().executeCommand("use " + applicationName);
        cr = getShell().executeCommand("add-alias --alias " + alias);
        String result = cr.getResult().toString();
        String expectedResult = "You are not connected to CloudUnit host! Please use connect command";
        Assert.assertTrue(result.contains(expectedResult));
    }

    @Test
    public void test10_shoudListAlias() {
        CommandResult cr = getShell().executeCommand("connect --login johndoe --password abc2015");
        cr = getShell().executeCommand("use " + applicationName);
        cr = getShell().executeCommand("list-aliases");
        String result = cr.getResult().toString();
        String expectedResult = "2 aliases found!";
        Assert.assertEquals(expectedResult, result);
    }

    @Test
    public void test11_shoudListAliasWithArgs() {
        CommandResult cr = getShell().executeCommand("connect --login johndoe --password abc2015");
        cr = getShell().executeCommand("list-aliases --name " + applicationName);
        String result = cr.getResult().toString();
        String expectedResult = "2 aliases found!";
        Assert.assertEquals(expectedResult, result);
    }

    @Test
    public void test12_shoudNotListAliasBecauseUserIsNotLogged() {
        CommandResult cr = getShell().executeCommand("use " + applicationName);
        cr = getShell().executeCommand("list-aliases");
        String result = cr.getResult().toString();
        String expectedResult = "You are not connected to CloudUnit host! Please use connect command";
        Assert.assertTrue(result.contains(expectedResult));
    }

    @Test
    public void test12_shoudNotListAliasBecauseApplicationNotSelected() {
        CommandResult cr = getShell().executeCommand("connect --login johndoe --password abc2015");
        cr = getShell().executeCommand("list-aliases");
        String result = cr.getResult().toString();
        String expectedResult = "No application is currently selected by the following command line : use <application name>";
        Assert.assertTrue(result.contains(expectedResult));
    }

    @Test
    public void test20_shoudRemoveAliasWithArgs() {
        CommandResult cr = getShell().executeCommand("connect --login johndoe --password abc2015");
        cr = getShell().executeCommand("use " + applicationName);
        cr = getShell().executeCommand("rm-alias --alias " + alias);
        String result = cr.getResult().toString();
        String expectedResult = "This alias has successful been deleted";
        Assert.assertEquals(expectedResult, result);
        cr = getShell().executeCommand("list-aliases --name " + applicationName);
        result = cr.getResult().toString();
        expectedResult = "1 aliases found!";
        Assert.assertEquals(expectedResult, result);
    }

    @Test
    public void test21_shoudNotRemoveAliasBecauseApplicationNotSelected() {
        CommandResult cr = getShell().executeCommand("connect --login johndoe --password abc2015");
        cr = getShell().executeCommand("rm-alias --alias " + alias);
        String result = cr.getResult().toString();
        String expectedResult = "No application is currently selected by the following command line : use <application name>";
        Assert.assertTrue(result.contains(expectedResult));
        cr = getShell().executeCommand("list-aliases --name " + applicationName);
        result = cr.getResult().toString();
        expectedResult = "1 aliases found!";
        Assert.assertEquals(expectedResult, result);
    }

    @Test
    public void test22_shoudNotRemoveAliasBecauseUserIsNotLogged() {
        CommandResult cr = getShell().executeCommand("create-app --name " + applicationName + " --type " + serverType);
        cr = getShell().executeCommand("use " + applicationName);
        cr = getShell().executeCommand("rm-alias --alias " + alias);
        String result = cr.getResult().toString();
        String expectedResult = "You are not connected to CloudUnit host! Please use connect command";
        Assert.assertTrue(result.contains(expectedResult));
        cr = getShell().executeCommand("connect --login johndoe --password abc2015");
        cr = getShell().executeCommand("list-aliases --name " + applicationName);
        result = cr.getResult().toString();
        expectedResult = "1 aliases found!";
        Assert.assertEquals(expectedResult, result);
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
