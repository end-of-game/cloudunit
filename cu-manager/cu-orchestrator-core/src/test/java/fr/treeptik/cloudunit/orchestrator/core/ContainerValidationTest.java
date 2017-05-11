package fr.treeptik.cloudunit.orchestrator.core;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import org.junit.Before;
import org.junit.Test;

import fr.treeptik.cloudunit.orchestrator.resource.ContainerResource;

public class ContainerValidationTest {
    private Validator validator;
    
    @Before
    public void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }
    
    @Test
    public void testValid() {
        ContainerResource resource = new ContainerResource("mycontainer", "tomcat-8");
        
        Set<ConstraintViolation<ContainerResource>> violations = validator.validate(resource);
        
        assertThat(violations, emptyIterable());
    }
}
