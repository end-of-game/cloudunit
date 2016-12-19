package fr.treeptik.cloudunit.cli.integration.server;

import static fr.treeptik.cloudunit.cli.integration.ShellMatchers.*;
import static org.junit.Assert.*;
import static org.junit.Assume.*;

import java.util.Random;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.shell.core.CommandResult;

import fr.treeptik.cloudunit.cli.integration.AbstractShellIntegrationTest;

/**
 * Created by guillaume on 16/10/15.
 */
public abstract class AbstractServerCommandsIT extends AbstractShellIntegrationTest {
    private String volumeName;

    protected AbstractServerCommandsIT(String serverType) {
        super(serverType);
    }

    @Before
    public void setUp() {
        volumeName = "volume" + new Random().nextInt(10000);
        connect();
    }

    @After
    public void tearDown() {
        disconnect();
    }

    @Test
    public void test_shouldChangeJavaVersion() {
        createApplication();
        try {
            CommandResult result = changeJavaVersion("java8");
            
            assertThat(result, isSuccessfulCommand());
        } finally {
            removeApplication();
        }
    }

    @Test
    public void test_shouldNotChangeJavaVersionBecauseApplicationNotSelected() {
        CommandResult result = changeJavaVersion("java8");
        
        assertThat(result, isFailedCommand());
    }

    @Test
    public void test_shouldNotChangeJavaVersionBecauseJdkValueDoesNotExist() {
        createApplication();
        try {
            CommandResult result = changeJavaVersion("jdk1.XXX");
            
            assertThat(result, isFailedCommand());
        } finally {
            removeApplication();
        }
    }

    @Test
    public void test_shouldChangeMemory() {
        createApplication();
        try {
            CommandResult result = changeJavaMemory(1024);
            
            assertThat(result, isSuccessfulCommand());
        } finally {
            removeApplication();
        }
    }

    @Test
    public void test_shouldNotChangeMemoryBecauseApplicationNotSelected() {
        CommandResult result = changeJavaMemory(1024);
        
        assertThat(result, isFailedCommand());
    }

    @Test
    public void test_shouldNotChangeMemoryBecauseJVMSizeIsNotAuthorized() {
        createApplication();
        try {
            CommandResult result = changeJavaMemory(128);
            
            assertThat(result, isFailedCommand());
        } finally {
            removeApplication();
        }
    }

    @Test
    public void test_shouldChangeJavaOpts() {
        createApplication();
        try {
            CommandResult result = addJvmOption("-Dkey=value");
            
            assertThat(result, isSuccessfulCommand());
        } finally {
            removeApplication();
        }
    }

    @Test
    public void test_shouldNotChangeJavaOptsBecauseApplicationNotSelected() {
        CommandResult result = addJvmOption("-Dkey=value");
        
        assertThat(result, isFailedCommand());
    }

    @Test
    public void test_shouldMountVolumeOnApplication() {
        createApplication();
        createVolume(volumeName);
        try {
            CommandResult result = mountVolume("/cloudunit/", volumeName);
            assertThat(result, isSuccessfulCommand());
        } finally {
            removeVolume(volumeName);
            removeApplication();
        }
    }

    @Test
    public void test_shouldMountAndUnmountVolumeOnApplication() {
        createApplication();
        createVolume(volumeName);
        try {            
            CommandResult result = mountVolume("/cloudunit/", volumeName);
            assumeThat(result, isSuccessfulCommand());
            
            result = unmountVolume("dev-johndoe-" + applicationName + "-" + serverType, volumeName);
            assertThat(result, isSuccessfulCommand());
        } finally {
            removeVolume(volumeName);
            removeApplication();
        }
    }

    @Test
    public void test_shouldNotMountVolumeOnApplicationPathEmpty() {
        createApplication();
        createVolume(volumeName);
        try {
            CommandResult result = mountVolume("", volumeName);
            
            assertThat(result, isFailedCommand());
        } finally {
            removeVolume(volumeName);
            removeApplication();
        }
    }

    @Test
    public void test_shouldNotMountVolumeOnApplicationVolumeNameEmpty() {
        createApplication();
        try {
            CommandResult result = mountVolume("/cloudunit/", "");
            
            assertThat(result, isFailedCommand());
        } finally {
            removeApplication();
        }
    }

    @Test
    public void test_shouldNotMountVolumeOnApplicationPathInconsistent() {
        createApplication();
        createVolume(volumeName);
        try {
            CommandResult result = mountVolume(".", volumeName);
            
            assertThat(result, isFailedCommand());
        } finally {
            removeVolume(volumeName);
            removeApplication();
        }
    }

    @Test
    public void test_shouldNotMountVolumeOnApplicationVolumeNameNonExistant() {
        createApplication();
        try {
            CommandResult result = mountVolume("/cloudunit/", "dzqmokdzq");
            
            assertThat(result, isFailedCommand());
        } finally {
            removeApplication();
        }
    }

    @Test
    public void test_shouldNotUnmountVolumeOnApplicationContainerNameEmpty() {
        createApplication();
        try {
            CommandResult result = unmountVolume("", volumeName);
            
            assertThat(result, isFailedCommand());
        } finally {
            removeApplication();
        }
    }

    @Test
    public void test_shouldNotUnmountVolumeOnApplicationVolumeNameEmpty() {
        createApplication();
        try {
            CommandResult result = unmountVolume("dev-johndoe-" + applicationName + "-" + serverType, "");
            
            assertThat(result, isFailedCommand());
        } finally {
            removeApplication();
        }
    }

    @Test
    public void test_shouldNotUnmountVolumeOnApplicationVolumeNameNotExistant() {
        createApplication();
        try {
            CommandResult result = unmountVolume("dev-johndoe-" + applicationName + "-" + serverType, "dzqmokdzq");
            
            assertThat(result, isFailedCommand());
        } finally {
            removeApplication();
        }
    }

    private CommandResult changeJavaVersion(String version) {
        return getShell().executeCommand(String.format("change-java-version --javaVersion %s", version));
    }

    private CommandResult changeJavaMemory(int memory) {
        return getShell().executeCommand(String.format("change-jvm-memory --size %s", memory));
    }

    private CommandResult addJvmOption(String option) {
        return getShell().executeCommand(String.format("add-jvm-option %s", option));
    }

    private CommandResult createVolume(String name) {
        return getShell().executeCommand(String.format("create-volume --name %s", name));
    }

    private CommandResult removeVolume(String name) {
        return getShell().executeCommand(String.format("rm-volume --name %s", name));
    }

    private CommandResult mountVolume(String path, String name) {
        return getShell().executeCommand(String.format("mount-volume --path %s --volume-name %s", path, name));
    }

    private CommandResult unmountVolume(String containerName, String name) {
        return getShell().executeCommand(String.format("unmount-volume --container-name %s --volume-name %s",
                containerName,
                name));
    }
}
