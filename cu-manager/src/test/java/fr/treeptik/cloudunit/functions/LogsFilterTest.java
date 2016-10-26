package fr.treeptik.cloudunit.functions;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import fr.treeptik.cloudunit.dto.LogResource;

/**
 * Created by nicolas on 04/01/2016.
 */
public class LogsFilterTest {

    private List<LogResource> logs;

    @Before
    public void init() {
        logs = new ArrayList<>();
        LogResource line1 = new LogResource("catalina.out", "Hello, I am a log stackstrace!");
        LogResource line2 = new LogResource("catalina.out", "Goodbye, I am an another message!");
        LogResource line3 = new LogResource("localhost.txt", "Goodbye, I am an wrong message!");
        LogResource line4 = new LogResource("localhost.txt", "Goodbye, I am an empty message!");

        logs = new ArrayList() {
            {
                add(line1);
                add(line2);
                add(line3);
                add(line4);
            }
        };
    }

    @Test
    public void testFilterSource() {
        List<LogResource> logsFiltered = LogsFilter.bySource.apply("catalina.out", logs);
        Assert.assertEquals(2, logsFiltered.size());

        logsFiltered = LogsFilter.bySource.apply("NO FILE", logs);
        Assert.assertEquals(0, logsFiltered.size());
    }

    @Test
    public void testFilterMessage() {
        List<LogResource> logsFiltered = LogsFilter.byMessage.apply("stackstrace", logs);
        Assert.assertEquals(1, logsFiltered.size());

        logsFiltered = LogsFilter.byMessage.apply("GOODB", logs);
        Assert.assertEquals(3, logsFiltered.size());

        logsFiltered = LogsFilter.bySource.apply("NOTHING HERE", logs);
        Assert.assertEquals(0, logsFiltered.size());
    }
}