package fr.treeptik.cloudunit.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * Returns a 401 error code (Unauthorized) to the client.
 */
@Component
public class Http401EntryPoint implements AuthenticationEntryPoint {

    private final Logger log = LoggerFactory.getLogger(Http401EntryPoint.class);

    private final String GRUNT_PROBLEM_LOGIN = System.getenv("GRUNT_PROBLEM_LOGIN");

    @Inject
    private Environment environment;

    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException arg) throws IOException,
            ServletException {

        // Maybe change the log level...
        log.warn("Access Denied [ " + request.getRequestURL().toString() + "] : " + arg.getMessage());

        // Trace message to ban intruders with fail2ban
        //generateLogTraceForFail2ban();

        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Access unauthorized");
    }

    public void generateLogTraceForFail2ban() {
        log.debug("generateLogTraceForFail2ban");
        String filePath = environment.getProperty("fail2ban.login.file");
        try {
            Files.write(Paths.get(filePath), "Access Denied".getBytes(), StandardOpenOption.APPEND);
            Files.write(Paths.get(filePath), System.getProperty("line.separator").getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            log.error("Cannot write to "+filePath+"", e.getMessage());
        }
    }
}
