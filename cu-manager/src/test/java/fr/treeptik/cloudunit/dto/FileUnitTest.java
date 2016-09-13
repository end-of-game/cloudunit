package fr.treeptik.cloudunit.dto;

import fr.treeptik.cloudunit.factory.EnvUnitFactory;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * Created by nicolas on 12/09/2016.
 */
public class FileUnitTest {

    @Test
    public void decode() {
        String output = "    total 72\n" +
                "    drwxr-xr-x  57 root root 4096 Sep 12 20:30 ./\n" +
                "    drwxr-xr-x  57 root root 4096 Sep 12 20:30 ../\n" +
                "    -rwxr-xr-x   1 root root    0 Sep 12 20:30 .dockerenv*\n" +
                "    drwxr-xr-x   2 root root 4096 Sep  9 08:56 bin/\n" +
                "    drwxr-xr-x   2 root root 4096 Apr 10  2014 boot/\n" +
                "    drwxr-xr-x   5 root root  360 Sep 12 20:30 dev/\n" +
                "    drwxr-xr-x  69 root root 4096 Sep 12 20:30 etc/\n" +
                "    drwxr-xr-x   2 root root 4096 Apr 10  2014 home/\n" +
                "    drwxr-xr-x  13 root root 4096 Sep  9 08:56 lib/\n" +
                "    drwxr-xr-x   2 root root 4096 Aug 19 03:39 lib64/\n" +
                "    drwxr-xr-x   2 root root 4096 Aug 19 03:38 media/\n" +
                "    drwxr-xr-x   2 root root 4096 Apr 10  2014 mnt/\n" +
                "    drwxr-xr-x  14 root root 4096 Sep 12 10:13 opt/\n" +
                "    dr-xr-xr-x 161 root root    0 Sep 12 20:30 proc/\n" +
                "    drwx------   4 root root 4096 Sep 12 10:13 root/\n" +
                "    drwxr-xr-x   8 root root 4096 Aug 26 18:49 run/\n" +
                "    drwxr-xr-x   2 root root 4096 Aug 26 18:49 sbin/\n" +
                "    drwxr-xr-x   2 root root 4096 Aug 19 03:38 srv/\n" +
                "    dr-xr-xr-x  13 root root    0 Sep  9 09:32 sys/\n" +
                "    drwxrwxrwt   3 root root 4096 Sep 12 20:30 tmp/\n" +
                "    drwxr-xr-x  16 root root 4096 Sep  9 08:56 usr/\n" +
                "    drwxr-xr-x  17 root root 4096 Sep 12 10:13 var/\n";

        List<EnvUnit> envUnits = EnvUnitFactory.fromOutput(output);
        Assert.assertEquals("Output should contains 6 CU env", 6, envUnits.size());
    }

}
