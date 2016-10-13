package fr.treeptik.cloudunit.cli.integration;

import java.util.Random;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.springframework.shell.Bootstrap;
import org.springframework.shell.core.CommandResult;
import org.springframework.shell.core.JLineShellComponent;

/**
 * Created by guillaume on 15/10/15.
 */
public class AbstractShellIntegrationTest {
    public static final String TEST_USER = "johndoe";
    public static final String TEST_PASSWORD = "abc2015";

    private JLineShellComponent shell;
    protected static String applicationName;

    protected final String serverType;
    
    protected AbstractShellIntegrationTest(String serverType) {
        this.serverType = serverType;
    }

    protected JLineShellComponent getShell() {
        return shell;
    }
    
    @BeforeClass
    public static void generateApplicationName() {
        applicationName = "App" + new Random().nextInt(10000);
    }

    @Before
    public void startUp() throws InterruptedException {
        Bootstrap bootstrap = new Bootstrap();
        shell = bootstrap.getJLineShellComponent();
    }

    @After
    public void shutdown() {
        shell.stop();
    }

    protected CommandResult connect() {
        return getShell().executeCommand(String.format("connect --login %s --password %s", TEST_USER, TEST_PASSWORD));
    }

    protected CommandResult createApplication() {
        return createApplication(applicationName);
    }
    
    protected CommandResult createApplication(String name) {
        return createApplication(name, serverType);
    }
    
    protected CommandResult createApplication(String name, String serverType) {
        return getShell().executeCommand(String.format("create-app --name %s --type %s", name, serverType));
    }

    protected CommandResult useApplication() {
        return useApplication(applicationName);
    }

    protected CommandResult useApplication(String name) {
        return getShell().executeCommand(String.format("use %s", name));
    }

    protected CommandResult removeApplication() {
        return removeApplication(false);
    }

    protected CommandResult removeApplication(boolean errorIfNotExists) {
        return getShell().executeCommand(String.format("rm-app --name %s --scriptUsage --errorIfNotExists %s", applicationName, errorIfNotExists));
    }
    
    protected CommandResult removeModule(String moduleName) {
        return getShell().executeCommand(String.format("rm-module --name %s", moduleName));
    }

    protected CommandResult addModule(String moduleName) {
        return getShell().executeCommand(String.format("add-module --name %s", moduleName));
    }

    protected CommandResult displayModules() {
        return getShell().executeCommand("display-modules");
    }

    protected CommandResult disconnect() {
        return getShell().executeCommand("disconnect");
    }

    protected CommandResult removeCurrentApplication() {
        return getShell().executeCommand("rm-app --scriptUsage");
    }

    protected CommandResult removeApplication(String app) {
        return removeApplication(app, false);
    }

    protected CommandResult removeApplication(String app, boolean errorIfNotExists) {
        return getShell().executeCommand(String.format("rm-app --name %s --scriptUsage --errorIfNotExists %s", app, errorIfNotExists));
    }

    protected CommandResult startApplication() {
        return getShell().executeCommand("start --name " + applicationName);
    }

    protected CommandResult startCurrentApplication() {
        return getShell().executeCommand("start");
    }

    protected CommandResult stopApplication() {
        return getShell().executeCommand("stop --name " + applicationName);
    }

    protected CommandResult stopApplication(String name) {
        return getShell().executeCommand(String.format("stop --name %s", name));
    }

    protected CommandResult stopCurrentApplication() {
        return getShell().executeCommand("stop");
    }

    protected CommandResult listContainers() {
        return getShell().executeCommand(String.format("list-containers --name %s", applicationName));
    }

    protected CommandResult listApplications() {
        return getShell().executeCommand("list-apps");
    }

    protected CommandResult information() {
        return getShell().executeCommand("informations");
    }
}
