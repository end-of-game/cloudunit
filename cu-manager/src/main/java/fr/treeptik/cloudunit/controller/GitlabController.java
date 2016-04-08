package fr.treeptik.cloudunit.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.treeptik.cloudunit.dto.HttpOk;
import fr.treeptik.cloudunit.dto.gitlab.GitlabPushEvents;
import fr.treeptik.cloudunit.exception.ServiceException;
import fr.treeptik.cloudunit.manager.ApplicationManager;
import fr.treeptik.cloudunit.model.User;
import fr.treeptik.cloudunit.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.servlet.http.HttpSession;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by nicolas on 07/04/2016.
 */

@RestController
@RequestMapping("/gitlab")
public class GitlabController {

    private static String GITLAB_IP = "192.168.50.4:480";
    private static String privateToken = "-Vv1_P1BWR_XaNytin1D";
    private static String CU_BACK_LINK = "192.168.2.106:8080";
    private AtomicInteger counter = new AtomicInteger(0);
    private Logger logger = LoggerFactory.getLogger(GitlabController.class);
    @Inject
    private AuthenticationManager authenticationManager;

    @Inject
    private ApplicationManager applicationManager;

    @Inject
    private UserService userService;

    /**
     * Print if a new branch is created in a project
     *
     * @param body String
     * @return String
     * @throws IOException
     */
    @RequestMapping(value = "/listen/branch", method = RequestMethod.POST)
    public String listenBranch(@RequestBody GitlabPushEvents body, @RequestParam String token, HttpSession session) throws IOException {
        if (token.equals("XXX")) {
            System.out.println(body);
            User user = null;
            try {
                user = userService.findByLogin(body.getUserName());
            } catch (ServiceException e) {
                logger.error(e.getLocalizedMessage());
            }

            Authentication authentication = null;
            if (user != null) {
                authentication = new UsernamePasswordAuthenticationToken(user.getLogin(), user.getPassword());
            }
            Authentication result = authenticationManager.authenticate(authentication);
            SecurityContext securityContext = SecurityContextHolder.getContext();
            securityContext.setAuthentication(result);
            session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, securityContext);
            try {
                if (body.getBefore().equals("0000000000000000000000000000000000000000")) {
                    System.out.println("New branch added for : " + token + ", sessionId : " + session.getId());
                } else {
                    System.out.println("New Build on existing branch for : " + token + ", sessionId : " + session.getId());
                }
                applicationManager.create(body.getUserName() + counter.incrementAndGet(), body.getUserName(), "tomcat-8");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return new HttpOk().toString();
    }

    /**
     * Print if a project is created and create a webhook for new branch in this project
     *
     * @param body String
     * @return String
     * @throws IOException
     */
    @RequestMapping(value = "/listen/project", method = RequestMethod.POST)
    public String listenProject(@RequestBody String body) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode actualObj = mapper.readTree(body);
        if (actualObj.has("event_name")) {
            JsonNode nodeEventName = actualObj.get("event_name");
            if (nodeEventName.get("event_name").toString().equals("project_create")) {
                System.out.println("New project added !");
                addWebhookById(nodeEventName.get("project_id").asInt());
                System.out.println("Webhook of project " + nodeEventName.get("project_id").asInt() + " created.");
            }
        }
        return "Listen project ok";
    }

    /**
     * Add a WebHook for an existing project
     *
     * @param id int
     * @return HttpStatus
     * @throws IOException
     */
    public HttpStatus addWebhookById(int id) {
        DataOutputStream wr = null;
        HttpURLConnection connPost = null;
        HttpStatus code = HttpStatus.EXPECTATION_FAILED;
        try {
            URL urlPost = new URL("http://" + GITLAB_IP + "/api/v3/projects/" +
                    id + "/hooks?private_token=" + privateToken);
            connPost = (HttpURLConnection) urlPost.openConnection();
            connPost.setDoOutput(true);
            connPost.setDoInput(true);
            connPost.setRequestProperty("Content-Type", "application/json");
            connPost.setRequestProperty("Accept", "application/json");
            connPost.setRequestMethod("POST");
            connPost.connect();

            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.createObjectNode();
            ((ObjectNode) rootNode).put("project_id", id);
            ((ObjectNode) rootNode).put("url", "http://" + CU_BACK_LINK + "/gitlab/listen/branch");
            ((ObjectNode) rootNode).put("push_events", true);
            ((ObjectNode) rootNode).put("enable_ssl_verification", true);
            String jsonString = mapper.writeValueAsString(rootNode);
            logger.debug(jsonString);
            System.out.println(jsonString);

            wr = new DataOutputStream(connPost.getOutputStream());
            wr.writeBytes(jsonString);
            code = HttpStatus.valueOf(connPost.getResponseCode());

        } catch (IOException e) {
            logger.error(e.getMessage());
        } finally {
            try {
                if (wr != null) wr.flush();
                if (wr != null) wr.close();
            } catch (Exception ignore) {
            }
        }
        return code;
    }

}
