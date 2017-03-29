package fr.treeptik.cloudunit.orchestrator.docker.test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.hateoas.Link;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.ObjectMapper;

import fr.treeptik.cloudunit.orchestrator.resource.VolumeResource;

@Component
@Lazy
public class VolumeTemplate {
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    public ResultActions createVolume(String name) throws Exception {
        VolumeResource request = new VolumeResource(name);
        ResultActions result = mockMvc.perform(post("/volumes")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON));
        return result;
    }
    
    public VolumeResource getVolume(ResultActions result) throws IOException {
        String content = result.andReturn().getResponse().getContentAsString();
        return objectMapper.readValue(content, VolumeResource.class);
    }
    
    public VolumeResource createAndAssumeVolume(String name) throws Exception {
        return getVolume(createVolume(name)
                .andExpect(status().isCreated()));
    }
    
    public ResultActions deleteVolume(VolumeResource volume) throws Exception {
        String uri = volume.getLink(Link.REL_SELF).getHref();
        return mockMvc.perform(delete(uri));
    }
}
