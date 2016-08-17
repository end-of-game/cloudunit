package fr.treeptik.cloudunit.cli.integration.user;

import fr.treeptik.cloudunit.cli.integration.AbstractShellIntegrationTest;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.shell.core.CommandResult;

/**
 * Created by guillaume on 15/10/15.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UserCommandsIT extends AbstractShellIntegrationTest {

    @Test
    public void test00_shouldConnect() {

        CommandResult cr = getShell().executeCommand("connect --login johndoe --password abc2015");

        String result = cr.getResult().toString();
        String expectedResult = "Connection established";

        Assert.assertEquals(result, expectedResult);
    }

    @Test
    public void test01_shouldNotConnectCausedByAlreadyConnected() {

        CommandResult cr = getShell().executeCommand("connect --login johndoe --password abc2015");
        cr = getShell().executeCommand("connect --login johndoe --password abc2015");
        String result = cr.getResult().toString();
        String expectedResult = "You are already connected to CloudUnit servers";

        Assert.assertTrue(result.contains(expectedResult));
    }


}
