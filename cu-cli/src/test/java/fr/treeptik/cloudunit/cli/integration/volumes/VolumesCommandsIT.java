package fr.treeptik.cloudunit.cli.integration.volumes;

import static fr.treeptik.cloudunit.cli.integration.ShellMatchers.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.junit.Assume.*;

import java.util.Random;

import org.junit.Before;
import org.junit.Test;
import org.springframework.shell.core.CommandResult;

import fr.treeptik.cloudunit.cli.integration.AbstractShellIntegrationTest;

public class VolumesCommandsIT extends AbstractShellIntegrationTest {
    private String volumeName;

    public VolumesCommandsIT() {
        super("tomcat-8");
    }

    @Before
    public void setUp() {
        volumeName = "Volume" + new Random().nextInt(100000);
        
        connect();
    }

    @Test
    public void test_createAndRemoveVolume() {
        // Create volume
        CommandResult result = createVolume(volumeName);
        
        assertThat("Volume created", result, isSuccessfulCommand());
        assertEquals(volumeName, result.getResult().toString());
        // List volumes and check
        result = listVolumes();
        assertThat("List of volumes contains volume name",
                result.getResult().toString(), containsString(volumeName));
        // Remove volume
        result = removeVolume(volumeName);
        
        assertThat(result, isSuccessfulCommand());
    }

    @Test
    public void test_two_identical_volumes() {
        // Create volume
        CommandResult result = createVolume(volumeName);
        try {
            assumeThat(result, isSuccessfulCommand());
            
            result = createVolume(volumeName);
            
            assertThat(result, isFailedCommand());
            assertThat(result.getException().getMessage(), containsString("already exists"));
        } finally {
            removeVolume(volumeName);
        }
    }


    @Test
    public void test_shouldNotCreateUnconsistentName() {
        // Create volume with wrong name
        CommandResult result = createVolume(volumeName + "/2");

        assertThat(result, isFailedCommand());
    }

    @Test
    public void test_shouldNotRemoveNonExistantName() {
        CommandResult result = removeVolume(volumeName);
        
        assertThat(result, isFailedCommand());
    }

    @Test
    public void test_shouldNotCreateEmptyName() {
        CommandResult result = createVolume("");
        
        assertThat(result, isFailedCommand());
    }
    
    @Test
    public void test_shouldNotCreateBlankName() {
        CommandResult result = createVolume("  ");
        
        assertThat(result, isFailedCommand());
    }

    @Test
    public void test_shouldNotRemoveEmptyName() {
        CommandResult result = removeVolume("");
        assertThat(result, isFailedCommand());
    }
    
    @Test
    public void test_shouldNotRemoveBlankName() {
        CommandResult result = removeVolume("  ");
        assertThat(result, isFailedCommand());
    }

    private CommandResult createVolume(String name) {
        return getShell().executeCommand(String.format("create-volume --name %s", name));
    }

    private CommandResult removeVolume(String name) {
        return getShell().executeCommand(String.format("rm-volume --name %s", name));
    }

    private CommandResult listVolumes() {
        return getShell().executeCommand("list-volumes");
    }
}
