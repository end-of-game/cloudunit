package fr.treeptik.cloudunit.dto;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

/**
 * Created by nicolas on 06/06/2016.
 */
public class EnvironmentVariableResourceTest {
    @Test
    public void decode() {
        String env = "HOSTNAME=58f99ebf2f88\n" +
                "CU_HOOKS=/cloudunit/appconf/hooks\n" +
                "CU_USER_HOME=/cloudunit/home\n" +
                "CU_JAVA=/cloudunit/java\n" +
                "PATH=/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin\n" +
                "CU_SHARED=/cloudunit/shared\n" +
                "PWD=/\n" +
                "SHLVL=1\n" +
                "HOME=/root\n" +
                "CU_SERVER_MANAGER_PORT=\n" +
                "CU_LOGS=/cloudunit/appconf/logs\n" +
                "CU_SCRIPTS=/cloudunit/scripts\n" +
                "_=/usr/bin/env\n";

        List<EnvironmentVariableResource> resources = EnvironmentVariableResource.fromEnv(env);
        
        assertThat(resources, iterableWithSize(13));
        assertThat(resources, hasItem(equalTo(new EnvironmentVariableResource("_", "/usr/bin/env"))));
    }

    @Test
    public void decodeEmpty() {
        String env = "";
        
        List<EnvironmentVariableResource> resources = EnvironmentVariableResource.fromEnv(env);
        
        assertThat(resources, empty());
    }
    
    @Test
    public void decodeBlank() {
        String env = "  ";
        
        List<EnvironmentVariableResource> resources = EnvironmentVariableResource.fromEnv(env);
        
        assertThat(resources, empty());
    }
    
    @Test
    public void decodeBlankLines() {
        String env = "\n";
        
        List<EnvironmentVariableResource> resources = EnvironmentVariableResource.fromEnv(env);
        
        assertThat(resources, empty());
    }
    
    @Test
    public void decodeNull() {
        String env = null;
        
        List<EnvironmentVariableResource> resources = EnvironmentVariableResource.fromEnv(env);
        
        assertThat(resources, empty());
    }
}
