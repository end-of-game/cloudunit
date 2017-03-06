package fr.treeptik.cloudunit.orchestrator.docker.test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.ObjectMapper;

import fr.treeptik.cloudunit.orchestrator.resource.ContainerResource;

@Component
public class ContainerTemplate {
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
        
        return getContainer(result);
    }
    
    public ResultActions deleteContainer(ContainerResource container) throws Exception {
        String url = container.getLink(Link.REL_SELF).getHref();
        return mockMvc.perform(delete(url));
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
}
