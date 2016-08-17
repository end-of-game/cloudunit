package fr.treeptik.cloudunit.cli.integration;

import org.junit.After;
import org.junit.Before;
import org.springframework.shell.Bootstrap;
import org.springframework.shell.core.JLineShellComponent;

/**
 * Created by guillaume on 15/10/15.
 */
public class AbstractShellIntegrationTest {

    private static JLineShellComponent shell;

    public static JLineShellComponent getShell() {
        return shell;
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


}
