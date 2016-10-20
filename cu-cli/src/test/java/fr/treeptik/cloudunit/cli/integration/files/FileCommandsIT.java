package fr.treeptik.cloudunit.cli.integration.files;

import static fr.treeptik.cloudunit.cli.integration.ShellMatchers.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.shell.core.CommandResult;

import fr.treeptik.cloudunit.cli.integration.AbstractShellIntegrationTest;

/**
 * Created by Nicolas
 */
public class FileCommandsIT extends AbstractShellIntegrationTest {

    public FileCommandsIT() {
        super("tomcat-8");
    }

    @Before
    public void setUp() {
        connect();
        createApplication();
    }

    @After
    public void tearDown() {
        removeApplication();
        disconnect();
    }

    @Test
    public void test_createDirectory() {
        openExplorer();
        try {
            createDirectory("/opt/cloudunit/temporary");
            changeDirectory("/opt/cloudunit");
            CommandResult result = listFiles();
            
            assertThat(result, isSuccessfulCommand());
            assertThat("Directory is created", result.getResult().toString(), containsString("temporary"));
        } finally {
            closeExplorer();
        }
    }

    @Test
    public void test_uploadFile() {
        openExplorer();
        try {
            changeDirectory("/opt");
            uploadPath("src/test/resources/my-beautiful-file.txt");
            CommandResult result = listFiles();
            
            assertThat(result, isSuccessfulCommand());
            assertThat("File is uploaded", result.getResult().toString(), containsString("my-beautiful-file.txt"));
        } finally {
            closeExplorer();
        }
    }

    @Test
    public void test_enterIntoContainerAndListFiles() {
        openExplorer();
        try {
            listFiles();
            changeDirectory("/etc");
            listFiles();
            changeDirectory("/opt");
            listFiles();
        } finally {
            closeExplorer();
        }
    }

    @Test
    public void test_unzip() {
        openExplorer();
        try {
            listFiles();
            changeDirectory("/opt/cloudunit");
            uploadPath("src/test/resources/compressed.tar");
            unzip("/opt/cloudunit/compressed.tar");
            CommandResult result = listFiles();
            
            assertThat(result, isSuccessfulCommand());
            assertThat("File is unzipped", result.getResult().toString(), containsString("my-beautiful-file.txt"));
        } finally {
            closeExplorer();
        }
    }

    private CommandResult listFiles() {
        return getShell().executeCommand("list-files");
    }

    private CommandResult unzip(String remotePathFile) {
        return getShell().executeCommand(String.format("unzip --file %s", remotePathFile));
    }

    private CommandResult changeDirectory(String path) {
        return getShell().executeCommand(String.format("change-directory %s", path));
    }

    private CommandResult createDirectory(String path) {
        return getShell().executeCommand(String.format("create-directory --path %s", path));
    }

    private CommandResult uploadPath(String pathFileToUpload) {
        return getShell().executeCommand(String.format("upload-file --path %s", pathFileToUpload));
    }

    private CommandResult openExplorer() {
        return getShell().executeCommand(String.format("open-explorer --containerName dev-johndoe-%s-%s",
                applicationName.toLowerCase(),
                serverType));
    }

    private CommandResult closeExplorer() {
        return getShell().executeCommand("close-explorer");
    }

}
