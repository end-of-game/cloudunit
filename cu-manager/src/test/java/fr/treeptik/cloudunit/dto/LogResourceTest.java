package fr.treeptik.cloudunit.dto;

import fr.treeptik.cloudunit.factory.EnvUnitFactory;
import fr.treeptik.cloudunit.factory.LogResourceFactory;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.List;

/**
 * Created by nicolas on 06/06/2016.
 */
public class LogResourceTest {

    @Test
    public void decode() {
        String output = "Line1\n" +
                "Line2\n" +
                "Line3\n";
        List<LogResource> logResources = LogResourceFactory.fromOutput(output);
        Assert.assertEquals("Output should contains 3 CU line", 3, logResources.size());
    }

    @Test
    public void decodeEmpty() {
        String output = "\n";
        List<LogResource> logResources = LogResourceFactory.fromOutput(output);
        Assert.assertEquals("Output should contains 0 CU line", 0, logResources.size());

        output = "\t";
        logResources = LogResourceFactory.fromOutput(output);
        Assert.assertEquals("Output should contains 0 CU line", 0, logResources.size());

        output = "";
        logResources = LogResourceFactory.fromOutput(output);
        Assert.assertEquals("Output should contains 0 CU line", 0, logResources.size());

        output = "  ";
        logResources = LogResourceFactory.fromOutput(output);
        Assert.assertEquals("Output should contains 0 CU line", 0, logResources.size());

        output = null;
        logResources = LogResourceFactory.fromOutput(output);
        Assert.assertEquals("Output should contains 0 CU env", 0, logResources.size());
    }
}
