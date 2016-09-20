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
        applicationName = "App" + new Random().nextInt(100000);
        volumeName = "Volume" + new Random().nextInt(100000);
    }

    @Before
    public void initEnv() {
        cr = getShell().executeCommand("connect --login johndoe --password abc2015 ");
    }

    @Test
    public void test_createAndRemoveVolume() {
        // Create volume
        createVolume(volumeName);
        String volumeNameCreated = cr.getResult().toString();
        Assert.assertTrue("Volume created : ", cr.isSuccess());
        Assert.assertEquals(volumeName, volumeNameCreated);
        // List volumes and check
        String listVolumes = listVolumes();
        Assert.assertTrue("List of volumes contains " + volumeName, listVolumes.contains(volumeName));
        // Remove volume
        removeVolume(volumeName);
    }

    @Test
    public void test_two_identical_volumes() {
        // Create volume
        createVolume(volumeName);
        createVolume(volumeName);
        String volumeNameCreated = cr.getResult().toString();
        Assert.assertTrue("Volume created : ", cr.isSuccess());
        Assert.assertTrue(volumeNameCreated.contains("This name already exists"));
        removeVolume(volumeName);
    }


    @Test
    public void test_shouldNotCreateUnconsistentName() {
        // Create volume with wrong name
        createVolume(volumeName + "/2");
        String volumeNameCreated = cr.getResult().toString();
        Assert.assertFalse("Volume not created : ", volumeNameCreated.equalsIgnoreCase(volumeName + "/2"));
    }

    @Test
    public void test_shouldNotRemoveNonExistantName() {
        removeVolume(volumeName);
        String result = cr.getResult().toString();
        String expectedResult = ANSIConstants.ANSI_RED + false + ANSIConstants.ANSI_RESET;
        Assert.assertEquals(expectedResult, result);
    }

    @Test
    public void test_shouldNotCreateEmptyName() {
        createVolume("");
        Assert.assertTrue("Command syntax incorrect: ", !cr.isSuccess());
        createVolume("  ");
        Assert.assertTrue("Command syntax incorrect: ", !cr.isSuccess());
    }

    @Test
    public void test_shouldNotRemoveEmptyName() {
        removeVolume("");
        Assert.assertTrue("Command syntax incorrect: ", !cr.isSuccess());
        removeVolume("  ");
        Assert.assertTrue("Command syntax incorrect: ", !cr.isSuccess());
    }


    private void createVolume(String name) {
        cr = getShell().executeCommand("create-volume --name " + name);
    }

    private void removeVolume(String name) {
        cr = getShell().executeCommand("rm-volume --name " + name);
    }

    private String listVolumes() {
        cr = getShell().executeCommand("list-volumes");
        Assert.assertTrue("List volumes", cr.isSuccess());
        return cr.getResult().toString();
    }
}
