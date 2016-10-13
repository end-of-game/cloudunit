package fr.treeptik.cloudunit.dto;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by nicolas on 30/09/15.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class LogLineTest {

    @Test(expected = IllegalArgumentException.class)
    public void parameterSourceNull() {
        LogResource line1 = new LogResource(null, "Hello, I am a log line or a stackstrace!");
    }

    @Test(expected = IllegalArgumentException.class)
    public void parameterMessageNull() {
        LogResource line1 = new LogResource("catalina.out", null);
    }

    @Test
    public void differentsLinesForMessage() {
        LogResource line1 = new LogResource("catalina.out", "Hello, I am a log stackstrace!");
        LogResource line2 = new LogResource("catalina.out", "Goodbye, I am an another message!");
        assertFalse(line1.equals(line2));

        LogResource line3 = new LogResource("catalina.out", "Hello, I am a log stackstrace!");
        LogResource line4 = new LogResource("localhost_20151203", "Hello, I am a log stackstrace!");
        assertFalse(line3.equals(line4));
    }

    @Test
    public void differentsLinesForDates() {
        LogResource line1 = new LogResource("catalina.out", "Hello, I am a log stackstrace!");
        LogResource line2 = new LogResource("localhost.txt", "Hello, I am a log stackstrace!");
        assertFalse(line1.equals(line2));
    }

}