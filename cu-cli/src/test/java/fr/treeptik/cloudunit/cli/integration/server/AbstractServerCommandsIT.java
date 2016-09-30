package fr.treeptik.cloudunit.cli.integration.server;

import java.util.Random;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ComparisonFailure;
import org.junit.Test;
import org.springframework.shell.core.CommandResult;

import fr.treeptik.cloudunit.cli.integration.AbstractShellIntegrationTest;

/**
 * Created by guillaume on 16/10/15.
 */
public abstract class AbstractServerCommandsIT extends AbstractShellIntegrationTest {
    private static String volumeName;
    private CommandResult cr;

    protected AbstractServerCommandsIT(String serverType) {
        super(serverType);
    }

    @Before
    public void initEnv() {
        volumeName = "volume" + new Random().nextInt(10000);
        cr = getShell().executeCommand("connect --login johndoe --password abc2015 ");
        createApplication();
    }

    @After
    public void tearDown() {
        deleteApplication();
    }

    @Test
    public void test00_shouldChangeJavaVersion() {
        useApplication(applicationName);
        changeJavaVersion("jdk1.8.0_25");
    }

    @Test(expected = ComparisonFailure.class)
    public void test01_shouldNotChangeJavaVersionBecauseApplicationNotSelected() {
        deleteApplication();
        changeJavaVersion("jdk1.8.0_25");
    }

    /*@Test
    public void test02_shouldNotChangeJavaVersionBecauseUserIsNotLogged() {
        CommandResult cr = getShell().executeCommand("use " + applicationName);
        cr = getShell().executeCommand("change-java-version --javaVersion jdk1.8.0_25");
        String result = cr.getResult().toString();
        String expectedResult = "You are not connected to CloudUnit host! Please use connect command";
        Assert.assertThat(result, Matchers.containsString(expectedResult));
    }*/

    @Test(expected = ComparisonFailure.class)
    public void test03_shouldNotChangeJavaVersionBecauseJDKValueDoesNotExist() {
        useApplication(applicationName);
        changeJavaVersion("jdk1.XXX");
    }

    @Test
    public void test10_shouldChangeMemory() {
        useApplication(applicationName);
        changeJavaMemory(1024);
    }

    @Test(expected = ComparisonFailure.class)
    public void test11_shouldNotChangeMemoryBecauseApplicationNotSelected() {
        deleteApplication();
        changeJavaMemory(1024);
    }

    /*@Test
    public void test12_shouldNotChangeMemoryBecauseUserIsNotLogged() {
        CommandResult cr = getShell().executeCommand("use " + applicationName);
        cr = getShell().executeCommand("change-jvm-memory --size 1024");
        String result = cr.getResult().toString();
        String expectedResult = "You are not connected to CloudUnit host! Please use connect command";
        Assert.assertThat(result, Matchers.containsString(expectedResult));

    }*/

    @Test(expected = ComparisonFailure.class)
    public void test10_shouldNotChangeMemoryBecauseJVMSizeIsNotAuthorized() {
        useApplication(applicationName);
        changeJavaMemory(128);
    }

    @Test
    public void test20_shouldChangeJavaOpts() {
        useApplication(applicationName);
        addJVMOption("-Dkey=value");
    }

    @Test(expected = ComparisonFailure.class)
    public void test21_shouldNotChangeJavaOptsBecauseApplicationNotSelected() {
        deleteApplication();
        addJVMOption("-Dkey=value");
    }

    @Test
    public void test30_mountAndUnmountVolumeOnApplication() {
        useApplication(applicationName);
        createVolume(volumeName);
        mountVolume("/cloudunit/", volumeName);
        unmountVolume("dev-johndoe-" + applicationName + "-" +
                serverType, volumeName);
        removeVolume(volumeName);
    }

    @Test(expected = NullPointerException.class)
    public void test31_shouldNotMountVolumeOnApplicationPathEmpty() {
        useApplication(applicationName);
        createVolume(volumeName);
        mountVolume("", volumeName);
        removeVolume(volumeName);
    }

    @Test(expected = NullPointerException.class)
    public void test32_shouldNotMountVolumeOnApplicationVolumeNameEmpty() {
        useApplication(applicationName);
        mountVolume("/cloudunit/", "");
        removeVolume(volumeName);
    }

    @Test(expected = ComparisonFailure.class)
    public void test33_shouldNotMountVolumeOnApplicationPathUnconsistent() {
        useApplication(applicationName);
        createVolume(volumeName);
        mountVolume(".", volumeName);
        removeVolume(volumeName);
    }

    @Test(expected = ComparisonFailure.class)
    public void test34_shouldNotMountVolumeOnApplicationVolumeNameNonExistant() {
        useApplication(applicationName);
        mountVolume("/cloudunit/", "volumeTest");
        removeVolume(volumeName);
    }

    @Test(expected = NullPointerException.class)
    public void test41_shouldNotUnmountVolumeOnApplicationContainerNameEmpty() {
        useApplication(applicationName);
        createVolume(volumeName);
        mountVolume("/cloudunit/", volumeName);
        unmountVolume("", volumeName);
        removeVolume(volumeName);
    }

    @Test(expected = ComparisonFailure.class)
    public void test42_shouldNotUnmountVolumeOnApplicationVolumeNameEmpty() {
        useApplication(applicationName);
        mountVolume("/cloudunit/", volumeName);
        unmountVolume("dev-johndoe-" + applicationName + "-" +
                serverType, "");
        removeVolume(volumeName);
    }

    @Test(expected = ComparisonFailure.class)
    public void test43_shouldNotUnmountVolumeOnApplicationVolumeNameNotExistant() {
        useApplication(applicationName);
        mountVolume("/cloudunit/", volumeName);
        unmountVolume("dev-johndoe-" + applicationName + "-" +
                serverType, "volumeTest");
        removeVolume(volumeName);
    }

    private void deleteApplication() {
        cr = getShell().executeCommand("rm-app --scriptUsage");
        Assert.assertTrue("Delete application", cr.isSuccess());
    }

    private void changeJavaVersion(String version) {
        cr = getShell().executeCommand("change-java-version --javaVersion " + version);
        String result = cr.getResult().toString();
        String expectedResult = "Your java version has been successfully changed";
        Assert.assertEquals(expectedResult, result);
    }

    private void changeJavaMemory(int memory) {
        cr = getShell().executeCommand("change-jvm-memory --size " + memory);
        String result = cr.getResult().toString();
        String expectedResult = "Change memory on " + applicationName.toLowerCase() + " successful";
        Assert.assertEquals(expectedResult, result);
    }

    private void addJVMOption(String option) {
        cr = getShell().executeCommand("add-jvm-option " + option);
        String result = cr.getResult().toString();
        String expectedResult = "Add java options to " + applicationName.toLowerCase() + " application successfully";
        Assert.assertEquals(expectedResult, result);
    }

    private void createVolume(String name) {
        cr = getShell().executeCommand("create-volume --name " + name);
        String result = cr.getResult().toString();
        String expectedResult = "The volume " + name + " was been successfully created";
        Assert.assertEquals(expectedResult, result);
    }

    private void removeVolume(String name) {
        cr = getShell().executeCommand("rm-volume --name " + name);
        String result = cr.getResult().toString();
        String expectedResult = "This volume has successful been deleted";
        Assert.assertEquals(expectedResult, result);
    }

    private void mountVolume(String path, String name) {
        cr = getShell().executeCommand("mount-volume --path " + path + " --volume-name " + name);
        String result = cr.getResult().toString();
        String expectedResult = "This volume has successful been mounted";
        Assert.assertEquals(expectedResult, result);
    }

    private void unmountVolume(String containerName, String name) {
        cr = getShell().executeCommand("unmount-volume --container-name " + containerName + " --volume-name " + name);
        String result = cr.getResult().toString();
        String expectedResult = "This volume has successful been unmounted";
        Assert.assertEquals(expectedResult, result);
    }
}
