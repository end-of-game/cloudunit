package fr.treeptik.cloudunit.test;

import static fr.treeptik.cloudunit.utils.TestUtils.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.io.FileInputStream;
import java.net.URL;

import javax.json.Json;

import org.apache.commons.io.FilenameUtils;
import org.springframework.hateoas.Resources;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.treeptik.cloudunit.dto.ApplicationResource;
import fr.treeptik.cloudunit.dto.DeploymentResource;
import fr.treeptik.cloudunit.dto.ModuleResource;
import fr.treeptik.cloudunit.dto.ServerResource;

public class ApplicationTemplate {
    private final ObjectMapper objectMapper;
    private final MockMvc mockMvc;
    private final MockHttpSession session;
    
    public ApplicationTemplate(ObjectMapper objectMapper, MockMvc mockMvc, MockHttpSession session) {
        this.objectMapper = objectMapper;
        this.mockMvc = mockMvc;
        this.session = session;
    }
    
    public ApplicationResource getApplication(ResultActions result) throws Exception {
        String content = result.andReturn().getResponse().getContentAsString();
        return objectMapper.readValue(content, ApplicationResource.class);
    }
    
    public ResultActions createApplication(String displayName, String serverType) throws Exception {
        String request = Json.createObjectBuilder()
                .add("displayName", displayName)
                .add("serverType", serverType)
                .build().toString();
                
        ResultActions result = mockMvc.perform(post("/applications")
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(request));
        return result;
    }
    
    public ApplicationResource createAndAssumeApplication(String displayName, String serverType) throws Exception {
        ResultActions result = createApplication(displayName, serverType);
        result.andExpect(status().isCreated());
        
        return getApplication(result);
    }
    
    public ApplicationResource refreshApplication(ApplicationResource application) throws Exception {
        String url = application.getId().getHref();
        
        ResultActions result = mockMvc.perform(get(url).session(session));
        result.andExpect(status().isOk());
        
        return getApplication(result);
    }
        
    public ResultActions stopApplication(ApplicationResource application) throws Exception {
        String url = application.getLink("stop").getHref();
        ResultActions result = mockMvc.perform(post(url).session(session));
        return result;
    }

    public ResultActions startApplication(ApplicationResource application) throws Exception {
        String url = application.getLink("start").getHref();
        ResultActions result = mockMvc.perform(post(url).session(session));
        return result;
    }

    public ResultActions removeApplication(ApplicationResource application) throws Exception {
        String url = application.getId().getHref();
        ResultActions result = mockMvc.perform(delete(url).session(session));
        return result;
    }
    
    public ResultActions addDeployment(ApplicationResource application, String contextPath, String warUrl)
            throws Exception {
        String url = application.getLink("deployments").getHref();
        String filename = FilenameUtils.getName(new URL(warUrl).getPath());
                
        ResultActions result = mockMvc.perform(fileUpload(url)
                .file(downloadAndPrepareFileToDeploy(filename, warUrl))
                .param("contextPath", contextPath)
                .session(session)
                .contentType(MediaType.MULTIPART_FORM_DATA));
        return result;
    }
    
    public DeploymentResource getDeployment(ResultActions result) throws Exception {
        String content = result.andReturn().getResponse().getContentAsString();
        return objectMapper.readValue(content, DeploymentResource.class);
    }
    
    public Resources<DeploymentResource> getDeployments(ApplicationResource application) throws Exception {
        String url = application.getLink("deployments").getHref();
        
        ResultActions result = mockMvc.perform(get(url).session(session));
        
        String content = result.andReturn().getResponse().getContentAsString();
        return objectMapper.readValue(content, new TypeReference<Resources<DeploymentResource>>() {});
    }
    
    public ServerResource getServer(ApplicationResource application) throws Exception {
        String url = application.getLink("server").getHref();
        ResultActions result = mockMvc.perform(get(url).session(session))
                .andExpect(status().isOk());
        
        return getServer(result);
    }

    public ServerResource getServer(ResultActions result) throws Exception {
        String content = result.andReturn().getResponse().getContentAsString();
        return objectMapper.readValue(content, ServerResource.class);
    }
    
    public ResultActions setJvmMemory(ServerResource server, Long jvmMemory) throws Exception {
        String request = Json.createObjectBuilder()
                .add("jvmMemory", jvmMemory)
                .build().toString();
        
        String url = server.getId().getHref();
        
        ResultActions result = mockMvc.perform(patch(url)
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(request));
        return result;
    }

    public ResultActions setJvmOptions(ServerResource server, String jvmOptions) throws Exception {
        String request = Json.createObjectBuilder()
                .add("jvmOptions", jvmOptions)
                .build().toString();
        
        String url = server.getId().getHref();
        
        ResultActions result = mockMvc.perform(patch(url)
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(request));
        return result;
    }
    
    public ResultActions setServer(ServerResource server) throws Exception {
        String request = objectMapper.writeValueAsString(server);
        
        String url = server.getId().getHref();
        
        ResultActions result = mockMvc.perform(put(url)
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(request));
        return result;
    }
            
    public ResultActions addModule(ApplicationResource application, String moduleName) throws Exception {
        String request = Json.createObjectBuilder()
                .add("name", moduleName)
                .build().toString();
        
        String url = application.getLink("modules").getHref();
        
        ResultActions result = mockMvc.perform(post(url)
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(request));
        return result;
    }
    
    public ModuleResource getModule(ResultActions result) throws Exception {
        String content = result.andReturn().getResponse().getContentAsString();
        return objectMapper.readValue(content, ModuleResource.class);
    }
    
    public Resources<ModuleResource> getModules(ApplicationResource application) throws Exception {
        String url = application.getLink("modules").getHref();
        
        ResultActions result = mockMvc.perform(get(url).session(session));
        result.andExpect(status().isOk());
        
        String content = result.andReturn().getResponse().getContentAsString();
        return objectMapper.readValue(content, new TypeReference<Resources<ModuleResource>>() {});
    }
    
    public ResultActions removeModule(ModuleResource module) throws Exception {
        String url = module.getId().getHref();
        
        ResultActions result = mockMvc.perform(delete(url).session(session));
        return result;
    }
    
    public ResultActions runScript(ModuleResource module, String path) throws Exception {
        String url = module.getLink("run-script").getHref();
        
        String filename = FilenameUtils.getName(path);
        
        MockMultipartFile file = new MockMultipartFile(
                "file",
                filename,
                "application/sql",
                new FileInputStream(path));

        ResultActions result = mockMvc.perform(fileUpload(url)
                .file(file)
                .session(session)
                .contentType(MediaType.MULTIPART_FORM_DATA));
        
        return result;
    }    
}
