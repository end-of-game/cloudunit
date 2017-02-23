package fr.treeptik.cloudunit.domain.test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import javax.json.Json;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.treeptik.cloudunit.domain.resource.ApplicationResource;

@Component
public class ApplicationTemplate {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    public ApplicationTemplate() {}
    
    public ApplicationTemplate(MockMvc mockMvc, ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }

    public ResultActions createApplication(String applicationName) throws Exception {
        String request = Json.createObjectBuilder()
            .add("name", applicationName)
            .build()
            .toString();
        
        ResultActions result = mockMvc.perform(
                post("/applications")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(request));
        
        return result;
    }
    
    protected <T> T getResult(ResultActions result, Class<T> type) throws Exception {
        String content = result.andReturn().getResponse().getContentAsString();
        return objectMapper.readValue(content, type);
    }
    
    protected <T> T getResult(ResultActions result, TypeReference<T> type) throws Exception {
        String content = result.andReturn().getResponse().getContentAsString();
        return objectMapper.readValue(content, type);
    }

    public ApplicationResource getApplication(ResultActions result) throws Exception {
        return getResult(result, ApplicationResource.class);
    }

    public ResultActions deleteApplication(ApplicationResource application) throws Exception {
        String url = application.getLink(Link.REL_SELF).getHref();
        ResultActions result = mockMvc.perform(delete(url));
        return result;
    }
    
}
