package fr.treeptik.cloudunit.domain.test;

import static org.awaitility.Awaitility.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import javax.json.Json;

import org.awaitility.Duration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.hateoas.Link;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.treeptik.cloudunit.domain.resource.ApplicationResource;
import fr.treeptik.cloudunit.domain.resource.ServiceResource;

@Component
public class ApplicationTemplate {
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired(required = false)
    @Qualifier("testApplicationName")
    private String testApplicationName;

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
    
    public ApplicationResource createAndAssumeApplication() throws Exception {
        return createAndAssumeApplication(testApplicationName);
    }
    
    public ApplicationResource createAndAssumeApplication(String applicationName) throws Exception {
        return getApplication(createApplication(applicationName)
                .andExpect(status().isCreated()));
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
    
    public ApplicationResource refreshApplication(ApplicationResource application) throws Exception {
        String uri = application.getLink(Link.REL_SELF).getHref();
        return getApplication(mockMvc.perform(get(uri)));
    }
    
    public ResultActions startApplication(ApplicationResource application) throws Exception {
        String uri = application.getLink("cu:start").getHref();
        return mockMvc.perform(post(uri));
    }

    public ResultActions stopApplication(ApplicationResource application) throws Exception {
        String uri = application.getLink("cu:stop").getHref();
        return mockMvc.perform(post(uri));
    }

    public ResultActions deleteApplication(ApplicationResource application) throws Exception {
        String url = application.getLink(Link.REL_SELF).getHref();
        ResultActions result = mockMvc.perform(delete(url));
        return result;
    }

    public ResultActions addService(ApplicationResource application, String serverName) throws Exception {
        String uri = application.getLink("cu:services").getHref();
        ServiceResource request = new ServiceResource(serverName);
        
        return mockMvc.perform(post(uri)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON));
    }

    public ServiceResource getService(ResultActions result) throws Exception {
        return getResult(result, ServiceResource.class);
    }

    public ServiceResource refreshService(ServiceResource service) throws Exception {
        String uri = service.getLink(Link.REL_SELF).getHref();
        return getService(mockMvc.perform(get(uri)));
    }

    public ResultActions removeService(ServiceResource service) throws Exception {
        String uri = service.getLink(Link.REL_SELF).getHref();
        return mockMvc.perform(delete(uri));
    }

    public void waitWhilePending(ApplicationResource application) {
        await().atMost(Duration.ONE_MINUTE).until(() -> !refreshApplication(application).getState().isPending());
    }
}
