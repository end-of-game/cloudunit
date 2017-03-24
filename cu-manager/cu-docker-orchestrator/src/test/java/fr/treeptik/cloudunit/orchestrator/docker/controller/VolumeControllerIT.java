package fr.treeptik.cloudunit.orchestrator.docker.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;
import org.springframework.test.web.servlet.ResultActions;

import fr.treeptik.cloudunit.orchestrator.docker.test.VolumeTemplate;
import fr.treeptik.cloudunit.orchestrator.resource.VolumeResource;

@SpringBootTest
@AutoConfigureMockMvc
public class VolumeControllerIT {
    @ClassRule
    public static final SpringClassRule SPRING_CLASS_RULE = new SpringClassRule();

    @Rule
    public final SpringMethodRule springMethodRule = new SpringMethodRule();

    @Autowired
    private VolumeTemplate volumeTemplate;
    
    @Test
    public void testCreateRemoveVolume() throws Exception {
        ResultActions result = volumeTemplate.createVolume("myvolume");
        result.andExpect(status().isCreated());
        
        VolumeResource volume = volumeTemplate.getVolume(result);
        
        result = volumeTemplate.deleteVolume(volume);
        result.andExpect(status().isNoContent());
    }
}
