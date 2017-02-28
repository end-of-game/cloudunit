package fr.treeptik.cloudunit.domain.model;

import static org.junit.Assert.*;

import org.junit.Test;

import fr.treeptik.cloudunit.domain.core.Module;
import fr.treeptik.cloudunit.domain.core.Server;
import fr.treeptik.cloudunit.domain.core.Service;
import fr.treeptik.cloudunit.domain.core.ServiceVisitor;

public class ServiceVisitorTest {
    @Test
    public void testAccept() {
        ServiceVisitor<String> serviceVisitor = new ServiceVisitor<String>() {
            
            @Override
            public String visitModule(Module module) {
                return "module";
            }
            
            @Override
            public String visitServer(Server server) {
                return "server";
            }
        };
        
        Service s = new Module();
        
        assertEquals("module", s.accept(serviceVisitor));
        assertEquals("server", new Server().accept(serviceVisitor));
    }
    
    @Test
    public void testRuntimeTypeOfTarget() {
        Object o = new Integer(3);
        
        assertEquals("3", o.toString());
    }
    
    @Test
    public void testRuntimeTypeOfArgument() {
        Integer o = new Integer(3);
        
        assertEquals("integer", new C().m(o));
    }
    
    public static class C {
        public String m(Object o) {
            return "object";
        }
        
        public String m(Integer i) {
            return "integer";
        }
    }
}
