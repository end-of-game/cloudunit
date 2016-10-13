package fr.treeptik.cloudunit.cli.integration.files;

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
    }

    @Test
    public void test_create_directory() {
        useApplication(applicationName);
        openExplorer();
        try {
            createDirectory("/opt/cloudunit/temporary");
            changeDirectory("/opt/cloudunit");
            CommandResult result = listFiles();
            
            assertThat("Directory is created", result.getResult().toString(), containsString("temporary"));
        } finally {
            closeExplorer();
        }
    }

    @Test
    public void test_enter_into_container_and_upload_file() {
        useApplication(applicationName);
        openExplorer();
        try {
            changeDirectory("/opt");
            uploadPath("src/test/resources/my-beautiful-file.txt");
            CommandResult result = listFiles();
            
            assertThat("File is uploaded", result.getResult().toString(), containsString("my-beautiful-file.txt"));
        } finally {
            closeExplorer();
        }
    }

    @Test
    public void test_enter_into_container_and_list_files() {
        useApplication(applicationName);
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
        useApplication(applicationName);
        openExplorer();
        try {
            listFiles();
            changeDirectory("/opt/cloudunit");
            uploadPath("src/test/resources/compressed.tar");
            unzip("/opt/cloudunit/compressed.tar");
            CommandResult result = listFiles();
            
            assertThat("File is unzipped", result.getResult().toString(), containsString("my-beautiful-file.txt"));
        } finally {
            closeExplorer();
        }
    }

    private CommandResult listFiles() {
        return getShell().executeCommand("list-files");
    }

    private CommandResult unzip(String remotePathFile) {
        return getShell().executeCommand("unzip --file " + remotePathFile);
    }

    private CommandResult changeDirectory(String path) {
        return getShell().executeCommand("change-directory " + path);
    }

    private CommandResult createDirectory(String path) {
        return getShell().executeCommand("create-directory --path " + path);
    }

    private CommandResult uploadPath(String pathFileToUpload) {
        return getShell().executeCommand("upload-file --path " + pathFileToUpload);
    }

    private CommandResult openExplorer() {
        return getShell().executeCommand("open-explorer --containerName dev-johndoe-" + applicationName + "-tomcat-8");
    }

    private CommandResult closeExplorer() {
        return getShell().executeCommand("close-explorer");
    }

}
