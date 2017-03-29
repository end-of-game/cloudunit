package fr.treeptik.cloudunit.orchestrator.docker.test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.hateoas.Resources;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.treeptik.cloudunit.orchestrator.resource.ImageResource;
import fr.treeptik.cloudunit.orchestrator.resource.VariableResource;

@Component
@Lazy
public class ImageTemplate {
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;

    public ImageResource getImage(String imageName) throws Exception {
        ResultActions result = mockMvc.perform(get("/images/{name}", imageName));
        String content = result.andReturn().getResponse().getContentAsString();
        return objectMapper.readValue(content, ImageResource.class);
    }

    public Resources<VariableResource> getVariables(ImageResource image) throws Exception {
        String uri = image.getLink("cu:variables").getHref();
        ResultActions result = mockMvc.perform(get(uri));
        String content = result.andReturn().getResponse().getContentAsString();
        return objectMapper.readValue(content, new TypeReference<Resources<VariableResource>>() {});
    }
}
