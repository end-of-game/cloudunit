package fr.treeptik.cloudunit.dto;

import static org.junit.Assert.*;

import fr.treeptik.cloudunit.dto.LogUnit;
import fr.treeptik.cloudunit.dto.LogLine;
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
        LogLine line1 = new LogLine(null, "Hello, I am a log line or a stackstrace!");
    }

    @Test(expected = IllegalArgumentException.class)
    public void parameterMessageNull() {
        LogLine line1 = new LogLine("catalina.out", null);
    }

    @Test
    public void differentsLinesForMessage() {
        LogLine line1 = new LogLine("catalina.out", "Hello, I am a log stackstrace!");
        LogLine line2 = new LogLine("catalina.out", "Goodbye, I am an another message!");
        assertFalse(line1.equals(line2));

        LogLine line3 = new LogLine("catalina.out", "Hello, I am a log stackstrace!");
        LogLine line4 = new LogLine("localhost_20151203", "Hello, I am a log stackstrace!");
        assertFalse(line3.equals(line4));
    }

    @Test
    public void differentsLinesForDates() {
        LogLine line1 = new LogLine("catalina.out", "Hello, I am a log stackstrace!");
        LogLine line2 = new LogLine("localhost.txt", "Hello, I am a log stackstrace!");
        assertFalse(line1.equals(line2));
    }

}