package fr.treeptik.cloudunit.orchestrator.docker.test;

import static org.awaitility.Awaitility.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.io.IOException;

import org.awaitility.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.treeptik.cloudunit.orchestrator.core.ContainerState;
import fr.treeptik.cloudunit.orchestrator.resource.ContainerResource;
import fr.treeptik.cloudunit.orchestrator.resource.MountResource;
import fr.treeptik.cloudunit.orchestrator.resource.VariableResource;
import fr.treeptik.cloudunit.orchestrator.resource.VolumeResource;

@Component
@Lazy
public class ContainerTemplate {
    private static final Logger LOGGER = LoggerFactory.getLogger(ContainerTemplate.class);

    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;

    public ResultActions createContainer(String name, String imageName) throws Exception {
        ContainerResource request = new ContainerResource(name, imageName);
        return mockMvc.perform(post("/containers")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON));
    }

    public ContainerResource getContainer(ResultActions result) throws Exception {
        String content = result.andReturn().getResponse().getContentAsString();
        return objectMapper.readValue(content, ContainerResource.class);
    }

    public ContainerResource createAndAssumeContainer(String name, String imageName) throws Exception {
        ResultActions result = createContainer(name, imageName);
        result.andExpect(status().isCreated());
        ContainerResource container = getContainer(result);
        waitWhilePending(container);
        return refreshContainer(container);
    }
    
    public ResultActions deleteContainer(ContainerResource container) throws Exception {
        String url = container.getLink(Link.REL_SELF).getHref();
        return mockMvc.perform(delete(url));
    }
    
    public void deleteContainerAndWait(ContainerResource container) throws Exception {
        deleteContainer(container);
        
        waitForRemoval(container);
    }

    public void waitForRemoval(ContainerResource container) {
        await().atMost(Duration.ONE_MINUTE).until(() -> {
            String uri = container.getLink(Link.REL_SELF).getHref();
            int status = mockMvc.perform(get(uri)).andReturn().getResponse().getStatus();
            LOGGER.debug("Container {} still exists? {}", container.getName(), status);
            return status == HttpStatus.NOT_FOUND.value();
        });
    }

    public ResultActions startContainer(ContainerResource container) throws Exception {
        String uri = container.getLink("cu:start").getHref();
        return mockMvc.perform(post(uri));
    }
    
    public ResultActions stopContainer(ContainerResource container) throws Exception {
        String uri = container.getLink("cu:stop").getHref();
        return mockMvc.perform(post(uri));
    }

    public ContainerResource refreshContainer(ContainerResource container) throws Exception {
        String uri = container.getLink(Link.REL_SELF).getHref();
        return getContainer(mockMvc.perform(get(uri)));
    }
    
    public void waitWhilePending(ContainerResource container) {
        await().atMost(Duration.ONE_MINUTE).until(() -> {
            ContainerState state = refreshContainer(container).getState();
            LOGGER.debug("Container {} is {}", container.getName(), state);
            return !state.isPending();
        });
    }

    public ResultActions addVariable(ContainerResource container, String key, String value) throws Exception {
        String uri = container.getLink("cu:variables").getHref();
        VariableResource variable = new VariableResource(key, value);
        return mockMvc.perform(post(uri)
                .content(objectMapper.writeValueAsString(variable))
                .contentType(MediaType.APPLICATION_JSON));
    }

    public VariableResource getVariable(ResultActions result) throws IOException {
        String content = result.andReturn().getResponse().getContentAsString();
        return objectMapper.readValue(content, VariableResource.class);
    }

    public Resources<VariableResource> getVariables(ContainerResource container) throws Exception {
        String uri = container.getLink("cu:variables").getHref();
        ResultActions result = mockMvc.perform(get(uri));
        String content = result.andReturn().getResponse().getContentAsString();
        return objectMapper.readValue(content, new TypeReference<Resources< VariableResource >>() {});
    }

    public ResultActions updateVariable(VariableResource variable, String key, String newValue) throws Exception {
        String uri = variable.getLink(Link.REL_SELF).getHref();
        
        variable.setValue(newValue);
        
        String request = objectMapper.writeValueAsString(variable);
        return mockMvc.perform(put(uri)
                .content(request)
                .contentType(MediaType.APPLICATION_JSON));
    }

    public ResultActions deleteVariable(VariableResource variable) throws Exception {
        String uri = variable.getLink(Link.REL_SELF).getHref();
        return mockMvc.perform(delete(uri));
    }
    
    public ResultActions mountVolume(ContainerResource container, VolumeResource volume, String mountPoint) throws Exception {
        String uri = container.getLink("cu:mounts").getHref();
        MountResource request = new MountResource(volume, mountPoint);
        
        ResultActions result = mockMvc.perform(post(uri)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON));
        return result;
    }
    
    public MountResource getMount(ResultActions result) throws IOException {
        String content = result.andReturn().getResponse().getContentAsString();
        return objectMapper.readValue(content, MountResource.class);
    }

    public ResultActions unmountVolume(MountResource mount) throws Exception {
        String uri = mount.getLink(Link.REL_SELF).getHref();
        return mockMvc.perform(delete(uri));
    }

}
