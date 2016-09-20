package fr.treeptik.cloudunit.dto;

import fr.treeptik.cloudunit.factory.EnvUnitFactory;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by nicolas on 06/06/2016.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class EnvUnitTest {

    @Test
    public void decode() {
        String output = "HOSTNAME=58f99ebf2f88\n" +
                "CU_HOOKS=/cloudunit/appconf/hooks\n" +
                "CU_USER_HOME=/cloudunit/home\n" +
                "CU_JAVA=/cloudunit/java\n" +
                "PATH=/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin\n" +
                "CU_SHARED=/cloudunit/shared\n" +
                "PWD=/\n" +
                "SHLVL=1\n" +
                "HOME=/root\n" +
                "CU_LOGS=/cloudunit/appconf/logs\n" +
                "CU_SCRIPTS=/cloudunit/scripts\n" +
                "_=/usr/bin/env\n";

        List<EnvUnit> envUnits = EnvUnitFactory.fromOutput(output);
        Assert.assertEquals("Output should contains 12 CU env", 12, envUnits.size());
    }

    @Test
    public void decodeEmpty() {
        String output = "";
        List<EnvUnit>  envUnits = EnvUnitFactory.fromOutput(output);
        Assert.assertEquals("Output should contains 0 CU env", 0, envUnits.size());

        output = "  ";
        envUnits = EnvUnitFactory.fromOutput(output);
        Assert.assertEquals("Output should contains 0 CU env", 0, envUnits.size());

        output = "\n";
        envUnits = EnvUnitFactory.fromOutput(output);
        Assert.assertEquals("Output should contains 0 CU env", 0, envUnits.size());

        output = "\t";
        envUnits = EnvUnitFactory.fromOutput(output);
        Assert.assertEquals("Output should contains 0 CU env", 0, envUnits.size());

        output = null;
        envUnits = EnvUnitFactory.fromOutput(output);
        Assert.assertEquals("Output should contains 0 CU env", 0, envUnits.size());
    }
}
