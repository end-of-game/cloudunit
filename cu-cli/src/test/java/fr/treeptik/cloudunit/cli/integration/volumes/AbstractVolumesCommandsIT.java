package fr.treeptik.cloudunit.cli.integration.volumes;

import fr.treeptik.cloudunit.cli.integration.AbstractShellIntegrationTest;
import fr.treeptik.cloudunit.cli.utils.ANSIConstants;
import org.junit.*;
import org.junit.runners.MethodSorters;
import org.springframework.shell.core.CommandResult;

import java.util.Random;

/**
 * Created by stagiaire on 31/08/16.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AbstractVolumesCommandsIT extends AbstractShellIntegrationTest {
    private static String applicationName;
    private static String volumeName;
    protected String serverType;

    @Before
    public void generateApplication() {
        applicationName = "App" + new Random().nextInt(10000);
    }
    @Before
    public void generateVolume() {
        volumeName = "Volume" + new Random().nextInt(10000);
    }

    @After
    public void removeApplication() {
        CommandResult cr = getShell().executeCommand("connect --login johndoe --password abc2015");
        cr = getShell().executeCommand("use " + applicationName);
        cr = getShell().executeCommand("rm-app --scriptUsage true");
    }

    @Test
    public void test00_createAndRemoveVolume() {
        CommandResult cr = getShell().executeCommand("connect --login johndoe --password abc2015");
        cr = getShell().executeCommand("create-volume --name " + volumeName);
        String result = cr.getResult().toString();
        String expectedResult = "The volume " + volumeName + " was been successfully created";
        Assert.assertEquals(expectedResult, result);
        cr = getShell().executeCommand("rm-volume --name " + volumeName);
        result = cr.getResult().toString();
        expectedResult = "This volume has successful been deleted";
        Assert.assertEquals(expectedResult, result);
    }

    @Test
    public void test01_shouldNotCreateUnconsistentName() {
        CommandResult cr = getShell().executeCommand("connect --login johndoe --password abc2015");
        cr = getShell().executeCommand("create-volume --name " + volumeName + "/2");
        String result = cr.getResult().toString();
        String expectedResult = ANSIConstants.ANSI_RED + "This name is not consistent : "+ volumeName + "/2" + ANSIConstants.ANSI_RESET;
        Assert.assertEquals(expectedResult, result);
    }

    @Test(expected = NullPointerException.class)
    public void test02_shouldNotCreateEmptyName() {
        CommandResult cr = getShell().executeCommand("connect --login johndoe --password abc2015");
        cr = getShell().executeCommand("create-volume --name");
        String result = cr.getResult().toString();
    }

    @Test
    public void test03_shouldNotRemoveNonExistantName() {
        CommandResult cr = getShell().executeCommand("connect --login johndoe --password abc2015");
        cr = getShell().executeCommand("rm-volume --name " + volumeName);
        String result = cr.getResult().toString();
        String expectedResult = ANSIConstants.ANSI_RED + "Volume doesn't exist" + ANSIConstants.ANSI_RESET;
        Assert.assertEquals(expectedResult, result);
    }

    @Test(expected = NullPointerException.class)
    public void test04_shouldNotRemoveEmptyName() {
        CommandResult cr = getShell().executeCommand("connect --login johndoe --password abc2015");
        cr = getShell().executeCommand("rm-volume --name");
        String result = cr.getResult().toString();
    }

    @Test
    public void test10_mountVolumeOnApplication() {
        CommandResult cr = getShell().executeCommand("connect --login johndoe --password abc2015");
        cr = getShell().executeCommand("use " + applicationName);
        cr = getShell().executeCommand("create-app --name " + applicationName + " --type " + serverType);
        cr = getShell().executeCommand("create-volume --name " + volumeName);
        String result = cr.getResult().toString();
        String expectedResult = "The volume " + volumeName + " was been successfully created";
        Assert.assertEquals(expectedResult, result);
        cr = getShell().executeCommand("mount-volume --path /cloudunit/ --volume-name " + volumeName);
        result = cr.getResult().toString();
        expectedResult = "This volume has successful been mounted";
        Assert.assertEquals(expectedResult, result);
    }

    @Test(expected = NullPointerException.class)
    public void test11_shouldNotMountVolumeOnApplicationPathEmpty() {
        CommandResult cr = getShell().executeCommand("connect --login johndoe --password abc2015");
        cr = getShell().executeCommand("use " + applicationName);
        cr = getShell().executeCommand("create-app --name " + applicationName + " --type " + serverType);
        cr = getShell().executeCommand("create-volume --name " + volumeName);
        String result = cr.getResult().toString();
        String expectedResult = "The volume " + volumeName + " was been successfully created";
        Assert.assertEquals(expectedResult, result);
        cr = getShell().executeCommand("mount-volume --path --volume-name " + volumeName);
        result = cr.getResult().toString();
    }

    @Test(expected = NullPointerException.class)
    public void test12_shouldNotMountVolumeOnApplicationVolumeNameEmpty() {
        CommandResult cr = getShell().executeCommand("connect --login johndoe --password abc2015");
        cr = getShell().executeCommand("use " + applicationName);
        cr = getShell().executeCommand("create-app --name " + applicationName + " --type " + serverType);
        cr = getShell().executeCommand("create-volume --name " + volumeName);
        String result = cr.getResult().toString();
        String expectedResult = "The volume " + volumeName + " was been successfully created";
        Assert.assertEquals(expectedResult, result);
        cr = getShell().executeCommand("mount-volume --path /cloudunit/ --volume-name");
        result = cr.getResult().toString();
    }

    @Test
    public void test13_shouldNotMountVolumeOnApplicationPathUnconsistent() {
        CommandResult cr = getShell().executeCommand("connect --login johndoe --password abc2015");
        cr = getShell().executeCommand("use " + applicationName);
        cr = getShell().executeCommand("create-app --name " + applicationName + " --type " + serverType);
        cr = getShell().executeCommand("create-volume --name " + volumeName);
        String result = cr.getResult().toString();
        String expectedResult = "The volume " + volumeName + " was been successfully created";
        Assert.assertEquals(expectedResult, result);
        cr = getShell().executeCommand("mount-volume --path . --volume-name " + volumeName);
        result = cr.getResult().toString();
        expectedResult = ANSIConstants.ANSI_RED + "This path is not consistent !" + ANSIConstants.ANSI_RESET;
        Assert.assertEquals(expectedResult, result);
    }

    @Test
    public void test14_shouldNotMountVolumeOnApplicationVolumeNameNonExistant() {
        CommandResult cr = getShell().executeCommand("connect --login johndoe --password abc2015");
        cr = getShell().executeCommand("use " + applicationName);
        cr = getShell().executeCommand("create-app --name " + applicationName + " --type " + serverType);
        cr = getShell().executeCommand("create-volume --name " + volumeName);
        String result = cr.getResult().toString();
        String expectedResult = "The volume " + volumeName + " was been successfully created";
        Assert.assertEquals(expectedResult, result);
        cr = getShell().executeCommand("mount-volume --path /cloudunit/ --volume-name volumeTest");
        result = cr.getResult().toString();
        expectedResult = ANSIConstants.ANSI_RED + "This volume does not exist" + ANSIConstants.ANSI_RESET;
        Assert.assertEquals(expectedResult, result);
    }

    @Test
    public void test20_unmountVolumeOnApplication() {
        CommandResult cr = getShell().executeCommand("connect --login johndoe --password abc2015");
        cr = getShell().executeCommand("use " + applicationName);
        cr = getShell().executeCommand("create-app --name " + applicationName + " --type " + serverType);
        cr = getShell().executeCommand("create-volume --name " + volumeName);
        String result = cr.getResult().toString();
        String expectedResult = "The volume " + volumeName + " was been successfully created";
        Assert.assertEquals(expectedResult, result);
        cr = getShell().executeCommand("mount-volume --path /cloudunit/ --volume-name " + volumeName);
        result = cr.getResult().toString();
        expectedResult = "This volume has successful been mounted";
        Assert.assertEquals(expectedResult, result);
        cr = getShell().executeCommand("unmount-volume --container-name dev-johndoe-" + applicationName + "-" +
                serverType + " --volume-name " + volumeName);
        result = cr.getResult().toString();
        expectedResult = "This volume has successful been unmounted";
        Assert.assertEquals(expectedResult, result);
    }

    @Test(expected = NullPointerException.class)
    public void test21_shouldNotUnmountVolumeOnApplicationContainerNameEmpty() {
        CommandResult cr = getShell().executeCommand("connect --login johndoe --password abc2015");
        cr = getShell().executeCommand("use " + applicationName);
        cr = getShell().executeCommand("create-app --name " + applicationName + " --type " + serverType);
        cr = getShell().executeCommand("create-volume --name " + volumeName);
        String result = cr.getResult().toString();
        String expectedResult = "The volume " + volumeName + " was been successfully created";
        Assert.assertEquals(expectedResult, result);
        cr = getShell().executeCommand("mount-volume --path /cloudunit/ --volume-name " + volumeName);
        result = cr.getResult().toString();
        expectedResult = "This volume has successful been mounted";
        Assert.assertEquals(expectedResult, result);
        cr = getShell().executeCommand("unmount-volume --container-name --volume-name " + volumeName);
        result = cr.getResult().toString();
    }

    @Test(expected = NullPointerException.class)
    public void test22_shouldNotUnmountVolumeOnApplicationVolumeNameEmpty() {
        CommandResult cr = getShell().executeCommand("connect --login johndoe --password abc2015");
        cr = getShell().executeCommand("use " + applicationName);
        cr = getShell().executeCommand("create-app --name " + applicationName + " --type " + serverType);
        cr = getShell().executeCommand("create-volume --name " + volumeName);
        String result = cr.getResult().toString();
        String expectedResult = "The volume " + volumeName + " was been successfully created";
        Assert.assertEquals(expectedResult, result);
        cr = getShell().executeCommand("mount-volume --path /cloudunit/ --volume-name " + volumeName);
        result = cr.getResult().toString();
        expectedResult = "This volume has successful been mounted";
        Assert.assertEquals(expectedResult, result);
        cr = getShell().executeCommand("unmount-volume --container-name dev-johndoe-" + applicationName + "-" +
                serverType + " --volume-name");
        result = cr.getResult().toString();
        expectedResult = ANSIConstants.ANSI_RED + ANSIConstants.ANSI_RESET;
        Assert.assertEquals(expectedResult, result);
    }

    @Test
    public void test23_shouldNotUnmountVolumeOnApplicationVolumeNameNotExistant() {
        CommandResult cr = getShell().executeCommand("connect --login johndoe --password abc2015");
        cr = getShell().executeCommand("use " + applicationName);
        cr = getShell().executeCommand("create-app --name " + applicationName + " --type " + serverType);
        cr = getShell().executeCommand("create-volume --name " + volumeName);
        String result = cr.getResult().toString();
        String expectedResult = "The volume " + volumeName + " was been successfully created";
        Assert.assertEquals(expectedResult, result);
        cr = getShell().executeCommand("mount-volume --path /cloudunit/ --volume-name " + volumeName);
        result = cr.getResult().toString();
        expectedResult = "This volume has successful been mounted";
        Assert.assertEquals(expectedResult, result);
        cr = getShell().executeCommand("unmount-volume --container-name dev-johndoe-" + applicationName + "-" +
                serverType + " --volume-name volumeTest");
        result = cr.getResult().toString();
        expectedResult = ANSIConstants.ANSI_RED + ANSIConstants.ANSI_RESET;
        Assert.assertEquals(expectedResult, result);
    }
}
