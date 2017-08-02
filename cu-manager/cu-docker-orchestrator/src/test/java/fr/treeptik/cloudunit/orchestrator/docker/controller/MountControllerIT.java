package fr.treeptik.cloudunit.orchestrator.docker.controller;

import static fr.treeptik.cloudunit.orchestrator.docker.test.TestCaseConstants.*;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;
import org.springframework.test.web.servlet.ResultActions;

import fr.treeptik.cloudunit.orchestrator.docker.test.ContainerTemplate;
import fr.treeptik.cloudunit.orchestrator.docker.test.VolumeTemplate;
import fr.treeptik.cloudunit.orchestrator.resource.ContainerResource;
import fr.treeptik.cloudunit.orchestrator.resource.MountResource;
import fr.treeptik.cloudunit.orchestrator.resource.VolumeResource;

@SpringBootTest
@AutoConfigureMockMvc
public class MountControllerIT {
    @ClassRule
    public static final SpringClassRule SPRING_CLASS_RULE = new SpringClassRule();

    @Rule
    public final SpringMethodRule springMethodRule = new SpringMethodRule();

    @Autowired
    private ContainerTemplate containerTemplate;
    
    @Autowired
    private VolumeTemplate volumeTemplate;

    @Autowired
    @Qualifier("testContainerName")
    private String containerName;

    @Autowired
    @Qualifier("testVolumeName")
    private String volumeName;

    
    @Test
    public void testMountVolume() throws Exception {
        VolumeResource volume = volumeTemplate.createAndAssumeVolume(volumeName);
        ContainerResource container = containerTemplate.createAndAssumeContainer(containerName, IMAGE_NAME);
        try {
            ResultActions result = containerTemplate.mountVolume(container, volume, "/etc");
            result.andExpect(status().isCreated());
            
            MountResource mount = containerTemplate.getMount(result);
            
            assertTrue(mount.hasLink("cu:volume"));
            assertNull(mount.getVolume());
        } finally {
            containerTemplate.waitWhilePending(container);
            containerTemplate.deleteContainerAndWait(container);
            volumeTemplate.deleteVolume(volume);
        }
    }
    
    @Test
    public void testMountUnmountVolume() throws Exception {
        VolumeResource volume = volumeTemplate.createAndAssumeVolume(volumeName);
        ContainerResource container = containerTemplate.createAndAssumeContainer(containerName, IMAGE_NAME);
        try {
            ResultActions result = containerTemplate.mountVolume(container, volume, "/etc");
            MountResource mount = containerTemplate.getMount(result);

            containerTemplate.waitWhilePending(container);
            
            result = containerTemplate.unmountVolume(mount);
            result.andExpect(status().isNoContent());
        } finally {
            containerTemplate.waitWhilePending(container);
            containerTemplate.deleteContainerAndWait(container);
            volumeTemplate.deleteVolume(volume);
        }
    }
}
