package fr.treeptik.cloudunit.domain.core;

import static org.junit.Assert.*;

import org.junit.Test;

public class ApplicationTest {
    
    @Test
    public void testStartingStart() {
        Application application = new Application("");
        
        boolean starting = application.start();
        boolean started = application.started();
        
        assertTrue("starting", starting);
        assertTrue("started", started);
    }
}
