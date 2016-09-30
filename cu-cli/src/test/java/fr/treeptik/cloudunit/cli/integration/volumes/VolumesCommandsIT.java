package fr.treeptik.cloudunit.cli.integration.volumes;

import static org.hamcrest.Matchers.*;

import java.util.Random;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.shell.core.CommandResult;

import fr.treeptik.cloudunit.cli.integration.AbstractShellIntegrationTest;
import fr.treeptik.cloudunit.cli.integration.ShellMatchers;
import fr.treeptik.cloudunit.cli.utils.ANSIConstants;

public class VolumesCommandsIT extends AbstractShellIntegrationTest {
    private static String volumeName;
    private CommandResult cr;

    protected VolumesCommandsIT(String serverType) {
        super(serverType);
    }

    @BeforeClass
    public static void generateVolumeName() {
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
        
        Assert.assertThat("Volume created : ", cr, ShellMatchers.isSuccessfulCommand());
        Assert.assertEquals(volumeName, volumeNameCreated);
        // List volumes and check
        String listVolumes = listVolumes();
        Assert.assertThat("List of volumes contains " + volumeName, listVolumes, containsString(volumeName));
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
        Assert.assertThat(volumeNameCreated, containsString("This name already exists"));
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
