package fr.treeptik.cloudunit.cli.integration.snapshot;

import static org.hamcrest.Matchers.*;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.shell.core.CommandResult;

import fr.treeptik.cloudunit.cli.integration.AbstractShellIntegrationTest;

/**
 * Created by guillaume on 16/10/15.
 */
public abstract class AbstractSnapshotCommandsIT extends AbstractShellIntegrationTest {    
    private static final String TAG_NAME = "myTag";

    protected AbstractSnapshotCommandsIT(String serverType) {
        super(serverType);
    }

    @Ignore
    @Test
    public void test00_shouldSnapshotAnApp() {
        CommandResult cr = getShell().executeCommand("connect --login johndoe --password abc2015");
        cr = getShell().executeCommand("create-app --name " + applicationName + " --type " + serverType);
        cr = getShell().executeCommand("use " + applicationName);
        cr = getShell().executeCommand("create-snapshot --tag " + TAG_NAME);
        String result = cr.getResult().toString();
        String expectedResult = "A new snapshot called " + TAG_NAME + " was successfully created.";
        Assert.assertEquals(expectedResult, result);
    }

    @Ignore
    @Test
    public void test01_shouldNotSnapshotAnAppBecauseUserIsNotLogged() {
        CommandResult cr = getShell().executeCommand("use " + applicationName);
        cr = getShell().executeCommand("create-snapshot --tag " + TAG_NAME);
        String result = cr.getResult().toString();
        String expectedResult = "You are not connected to CloudUnit host! Please use connect command";
        Assert.assertThat(result, containsString(expectedResult));
    }

    @Ignore
    @Test
    public void test02_shouldNotSnapshotAnAppBecauseApplicationNotSelected() {
        CommandResult cr = getShell().executeCommand("connect --login johndoe --password abc2015");
        cr = getShell().executeCommand("create-snapshot --tag " + TAG_NAME);
        String result = cr.getResult().toString();
        String expectedResult = "No application is currently selected by the following command line : use <application name>";
        Assert.assertThat(result, containsString(expectedResult));
    }

    @Ignore
    @Test
    public void test03_shouldNotSnapshotAnAppBecauseTagNameAlreadyExists() {
        CommandResult cr = getShell().executeCommand("connect --login johndoe --password abc2015");
        cr = getShell().executeCommand("use " + applicationName);
        cr = getShell().executeCommand("create-snapshot --tag " + TAG_NAME);
        String result = cr.getResult().toString();
        String expectedResult = "this tag already exists";
        Assert.assertThat(result, containsString(expectedResult));
    }

    @Ignore
    @Test
    public void test10_shouldCloneAnApp() {
        CommandResult cr = getShell().executeCommand("connect --login johndoe --password abc2015");
        cr = getShell().executeCommand("clone --tag " + TAG_NAME + " --applicationName " + applicationName + "cloned");
        String result = cr.getResult().toString();
        String expectedResult = "Your application " + applicationName + "cloned was successfully created.";
        Assert.assertEquals(expectedResult, result);
    }

    @Ignore
    @Test
    public void test11_shouldNotCloneAnAppBecauseTagNameDoesNotExist() {
        CommandResult cr = getShell().executeCommand("connect --login johndoe --password abc2015");
        cr = getShell().executeCommand("clone --tag " + TAG_NAME + "1 --applicationName " + applicationName + "cloned2");
        String result = cr.getResult().toString();
        String expectedResult = "This tag does not exist yet";
        Assert.assertThat(result, containsString(expectedResult));
    }

    @Ignore
    @Test
    public void test12_shouldNotCloneAnAppBecauseUserIsNotLogged() {
        CommandResult cr = getShell().executeCommand("clone --tag " + TAG_NAME + "1 --applicationName " + applicationName + "cloned2");
        String result = cr.getResult().toString();
        String expectedResult = "You are not connected to CloudUnit host! Please use connect command";
        Assert.assertThat(result, containsString(expectedResult));
    }

    @Ignore
    @Test
    public void test20_shouldListSnapshots() {
        CommandResult cr = getShell().executeCommand("connect --login johndoe --password abc2015");
        cr = getShell().executeCommand("list-snapshot");
        String result = cr.getResult().toString();
        String expectedResult = "1 snapshots found";
        Assert.assertEquals(expectedResult, result);
    }

    @Ignore
    @Test
    public void test21_shouldNotListSnapshotsBecauseUserIsNotLogged() {
        CommandResult cr = getShell().executeCommand("list-snapshot");
        String result = cr.getResult().toString();
        String expectedResult = "You are not connected to CloudUnit host! Please use connect command";
        Assert.assertThat(result, Matchers.containsString(expectedResult));
    }

    @Ignore
    @Test
    public void test30_shouldRemoveSnapshot() {
        CommandResult cr = getShell().executeCommand("connect --login johndoe --password abc2015");
        System.out.println("applicationName = " + applicationName);
        getShell().executeCommand("rm-app --name " + applicationName.toLowerCase() + "cloned --scriptUsage" );
        cr = getShell().executeCommand("rm-snapshot --tag " + TAG_NAME);
        String result = cr.getResult().toString();
        String expectedResult = "The snapshot myTag was successfully deleted.";
        Assert.assertEquals(expectedResult, result);
    }

    @Ignore
    @Test
    public void test90_cleanEnv() {
        CommandResult cr = getShell().executeCommand("connect --login johndoe --password abc2015");
        cr = getShell().executeCommand("rm-app --name " + applicationName + " --scriptUsage");
        String result = cr.getResult().toString();
        String expectedResult = "Your application " + applicationName.toLowerCase() + " is currently being removed";
        Assert.assertEquals(expectedResult, result);
    }
}
