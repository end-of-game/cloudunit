package fr.treeptik.cloudunit.orchestrator.core;

import static org.junit.Assert.*;

import org.junit.Test;

public class VariableRoleTest {
    @Test
    public void testPasswordGenerateValue() {
        Image image = Image.of("tomcat", "8", ImageType.SERVER, "cloudunit/tomcat:8").build();
        Container container = new Container("mycontainer", image);
        
        String value = VariableRole.PASSWORD.generateValue(container);
        
        System.out.println(value);
        
        assertTrue(value.matches("[0-9a-f]{8}"));
    }
}
