package fr.treeptik.cloudunit.utils;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by nicolas on 13/05/2016.
 */
public class JvmOptionsUtilsTest {

    @Test
    public void extractDir() {
        String dir = JvmOptionsUtils.extractDirectory("-Dcloudunit.shared=tartapion");
        Assert.assertEquals("tartapion", dir);
    }

    @Test
    public void extractDirWithOtherParams() {
        String dir = JvmOptionsUtils.extractDirectory("-Dkey=value -Dcloudunit.shared=tartapion");
        Assert.assertEquals("tartapion", dir);

        dir = JvmOptionsUtils.extractDirectory("-Dkey=value -Dcloudunit.shared=tartapion -Dkey2=value2");
        Assert.assertEquals("tartapion", dir);
    }

    @Test
    public void missingInput() {
        String dir = JvmOptionsUtils.extractDirectory("-Dkey=value");
        Assert.assertNull(dir);

        dir = JvmOptionsUtils.extractDirectory("-Dcloudunit.shared=");
        Assert.assertNull(dir);

        dir = JvmOptionsUtils.extractDirectory("-Dcloudunit.shared= -Dkey=value");
        Assert.assertNull(dir);

        dir = JvmOptionsUtils.extractDirectory("");
        Assert.assertNull(dir);

        dir = JvmOptionsUtils.extractDirectory("    ");
        Assert.assertNull(dir);

        dir = JvmOptionsUtils.extractDirectory("");
        Assert.assertNull(dir);

        dir = JvmOptionsUtils.extractDirectory("-Dkey=value -Dkey2=value2");
        Assert.assertNull(dir);
    }
}