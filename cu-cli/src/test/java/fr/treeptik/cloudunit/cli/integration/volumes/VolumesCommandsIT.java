package fr.treeptik.cloudunit.cli.integration.volumes;

import fr.treeptik.cloudunit.cli.integration.AbstractShellIntegrationTest;
import fr.treeptik.cloudunit.cli.utils.ANSIConstants;
import org.junit.*;
import org.junit.runners.MethodSorters;
import org.springframework.shell.core.CommandResult;

import java.util.Random;

public class VolumesCommandsIT extends AbstractShellIntegrationTest {

    private static String applicationName;
    private static String volumeName;
    private CommandResult cr;

    @BeforeClass
    public static void generateApplication() {
        applicationName = "App" + new Random().nextInt(10000);
        volumeName = "Volume" + new Random().nextInt(10000);
    }

    @Before
    public void initEnv() {
        cr = getShell().executeCommand("connect --login johndoe --password abc2015 ");
    }

    @Test
    public void test_createAndRemoveVolume() {
        createVolume(volumeName);
        String filesTxt = listVolumes();
        Assert.assertTrue("Volume is right created", filesTxt.contains("1"));
        removeVolume(volumeName);
    }

    @Test(expected = ComparisonFailure.class)
    public void test_shouldNotCreateUnconsistentName() {
        createVolume(volumeName + "/2");
    }

    @Test(expected = NullPointerException.class)
    public void test_shouldNotCreateEmptyName() {
        createVolume("");
    }

    @Test(expected = ComparisonFailure.class)
    public void test_shouldNotRemoveNonExistantName() {
        removeVolume(volumeName);
    }

    @Test(expected = NullPointerException.class)
    public void test_shouldNotRemoveEmptyName() {
        removeVolume("");
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

    private String listVolumes() {
        cr = getShell().executeCommand("list-volumes");
        Assert.assertTrue("List volumes", cr.isSuccess());
        return cr.getResult().toString();
    }
}
