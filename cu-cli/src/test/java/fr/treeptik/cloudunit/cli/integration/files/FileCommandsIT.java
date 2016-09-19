package fr.treeptik.cloudunit.cli.integration.files;

import fr.treeptik.cloudunit.cli.integration.AbstractShellIntegrationTest;
import org.junit.*;
import org.junit.runners.MethodSorters;
import org.springframework.shell.core.CommandResult;

import java.io.File;
import java.util.Random;

/**
 * Created by Nicolas
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class FileCommandsIT extends AbstractShellIntegrationTest {

    private static String applicationName;
    protected String serverType = "tomcat-8";
    private CommandResult cr = null;

    @BeforeClass
    public static void generateApplication() {
        applicationName = "app" + new Random().nextInt(10000);
    }

    @Before
    public void initEnv() {
        cr = getShell().executeCommand("connect --login johndoe --password abc2015 ");
        createApplication();
    }

    @After
    public void tearDown() {
        deleteApplication();
    }

    @Test
    public void test_create_directory() {
        useApplication();
        openExplorer();
        createDirectory("/opt/cloudunit/temporary");
        changeDirectory("/opt/cloudunit");
        String filesTxt = listFiles();
        Assert.assertTrue("Directory is right created", filesTxt.contains("temporary"));
        closeExplorer();
    }

    @Test
    public void test_enter_into_container_and_upload_file() {
        useApplication();
        openExplorer();
        changeDirectory("/opt");
        File local = new File(".");
        String pathFileToUpload = local.getAbsolutePath()
                +"/src/test/java/fr/treeptik/cloudunit/cli/integration/files/my-beautiful-file.txt";
        uploadPath(pathFileToUpload);
        String filesTxt = listFiles();
        Assert.assertTrue("File is right uploaded", filesTxt.contains("my-beautiful-file.txt"));
        closeExplorer();
    }

    @Test
    public void test_enter_into_container_and_list_files() {
        useApplication();
        openExplorer();
        listFiles();
        changeDirectory("/etc");
        listFiles();
        changeDirectory("/opt");
        listFiles();
        closeExplorer();
    }

    @Test
    public void test_unzip() {
        useApplication();
        openExplorer();
        listFiles();
        changeDirectory("/opt/cloudunit");
        File local = new File(".");
        String pathFileToUpload = local.getAbsolutePath()
                +"/src/test/java/fr/treeptik/cloudunit/cli/integration/files/compressed.tar";
        uploadPath(pathFileToUpload);
        unzip("/opt/cloudunit/compressed.tar");
        String filesTxt = listFiles();
        Assert.assertTrue("File is right unziped", filesTxt.contains("my-beautiful-file.txt"));
        closeExplorer();
    }

    @Test
    public void test_edit_and_save() {
        Assert.fail();
    }

    @Test
    public void test_download() {
        Assert.fail();
    }

    private void useApplication() {
        cr = getShell().executeCommand("use " + applicationName);
        Assert.assertTrue("Use Application", cr.isSuccess());
    }

    private String listFiles() {
        cr = getShell().executeCommand("list-files");
        Assert.assertTrue("List files", cr.isSuccess());
        return cr.getResult().toString();
    }

    private void unzip(String remotePathFile) {
        cr = getShell().executeCommand("unzip --file " + remotePathFile);
        Assert.assertTrue("Create Application", cr.isSuccess());
    }

    private void createApplication() {
        cr = getShell().executeCommand("create-app --name " + applicationName + " --type " + serverType);
        Assert.assertTrue("Create Application", cr.isSuccess());
    }

    private void deleteApplication() {
        cr = getShell().executeCommand("rm-app --scriptUsage");
        Assert.assertTrue("Delete application", cr.isSuccess());
    }

    private void changeDirectory(String path) {
        cr = getShell().executeCommand("change-directory " + path);
        Assert.assertTrue("Change directory", cr.isSuccess());
    }

    private void createDirectory(String path) {
        cr = getShell().executeCommand("create-directory --path " + path);
        Assert.assertTrue("Create directory", cr.isSuccess());
    }

    private void uploadPath(String pathFileToUpload) {
        cr = getShell().executeCommand("upload-file --path " + pathFileToUpload);
        Assert.assertTrue("Upload File", cr.isSuccess());
    }

    private void openExplorer() {
        cr = getShell().executeCommand("open-explorer --containerName dev-johndoe-" + applicationName + "-tomcat-8");
        Assert.assertTrue("Open explorer", cr.isSuccess());
    }

    private void closeExplorer() {
        cr = getShell().executeCommand("close-explorer");
        Assert.assertTrue("Close explorer", cr.isSuccess());
    }

}
