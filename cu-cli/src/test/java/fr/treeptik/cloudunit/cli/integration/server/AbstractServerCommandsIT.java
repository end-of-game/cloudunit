package fr.treeptik.cloudunit.cli.integration.server;

import fr.treeptik.cloudunit.cli.integration.AbstractShellIntegrationTest;
import fr.treeptik.cloudunit.dto.Command;
import org.junit.*;
import org.junit.runners.MethodSorters;
import org.springframework.shell.core.CommandResult;

import java.util.Random;

/**
 * Created by guillaume on 16/10/15.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public abstract class AbstractServerCommandsIT extends AbstractShellIntegrationTest {

    private static String applicationName;
    private static String volumeName;
    protected String serverType;
    private CommandResult cr;

    @BeforeClass
    public static void generateApplication() {
        applicationName = "app" + new Random().nextInt(10000);
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
        useApplication();
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
        Assert.assertTrue(result.contains(expectedResult));
    }*/

    @Test(expected = ComparisonFailure.class)
    public void test03_shouldNotChangeJavaVersionBecauseJDKValueDoesNotExist() {
        useApplication();
        changeJavaVersion("jdk1.XXX");
    }

    @Test
    public void test10_shouldChangeMemory() {
        useApplication();
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
        Assert.assertTrue(result.contains(expectedResult));

    }*/

    @Test(expected = ComparisonFailure.class)
    public void test10_shouldNotChangeMemoryBecauseJVMSizeIsNotAuthorized() {
        useApplication();
        changeJavaMemory(128);
    }

    @Test
    public void test20_shouldChangeJavaOpts() {
        useApplication();
        addJVMOption("-Dkey=value");
    }

    @Test(expected = ComparisonFailure.class)
    public void test21_shouldNotChangeJavaOptsBecauseApplicationNotSelected() {
        deleteApplication();
        addJVMOption("-Dkey=value");
    }

    /*@Test
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
    }*/


    @Test
    public void test30_mountAndUnmountVolumeOnApplication() {
        useApplication();
        createVolume(volumeName);
        mountVolume("/cloudunit/", volumeName);
        unmountVolume("dev-johndoe-" + applicationName + "-" +
                serverType, volumeName);
        removeVolume(volumeName);
    }

    @Test(expected = NullPointerException.class)
    public void test31_shouldNotMountVolumeOnApplicationPathEmpty() {
        useApplication();
        createVolume(volumeName);
        mountVolume("", volumeName);
        removeVolume(volumeName);
    }

    @Test(expected = NullPointerException.class)
    public void test32_shouldNotMountVolumeOnApplicationVolumeNameEmpty() {
        useApplication();
        mountVolume("/cloudunit/", "");
        removeVolume(volumeName);
    }

    @Test(expected = ComparisonFailure.class)
    public void test33_shouldNotMountVolumeOnApplicationPathUnconsistent() {
        useApplication();
        createVolume(volumeName);
        mountVolume(".", volumeName);
        removeVolume(volumeName);
    }

    @Test(expected = ComparisonFailure.class)
    public void test34_shouldNotMountVolumeOnApplicationVolumeNameNonExistant() {
        useApplication();
        mountVolume("/cloudunit/", "volumeTest");
        removeVolume(volumeName);
    }

    @Test(expected = NullPointerException.class)
    public void test41_shouldNotUnmountVolumeOnApplicationContainerNameEmpty() {
        useApplication();
        createVolume(volumeName);
        mountVolume("/cloudunit/", volumeName);
        unmountVolume("", volumeName);
        removeVolume(volumeName);
    }

    @Test(expected = ComparisonFailure.class)
    public void test42_shouldNotUnmountVolumeOnApplicationVolumeNameEmpty() {
        useApplication();
        mountVolume("/cloudunit/", volumeName);
        unmountVolume("dev-johndoe-" + applicationName + "-" +
                serverType, "");
        removeVolume(volumeName);
    }

    @Test(expected = ComparisonFailure.class)
    public void test43_shouldNotUnmountVolumeOnApplicationVolumeNameNotExistant() {
        useApplication();
        mountVolume("/cloudunit/", volumeName);
        unmountVolume("dev-johndoe-" + applicationName + "-" +
                serverType, "volumeTest");
        removeVolume(volumeName);
    }

    private void useApplication() {
        cr = getShell().executeCommand("use " + applicationName);
        Assert.assertTrue("Use Application", cr.isSuccess());
    }

    private void createApplication() {
        cr = getShell().executeCommand("create-app --name " + applicationName + " --type " + serverType);
        Assert.assertTrue("Create Application", cr.isSuccess());
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
