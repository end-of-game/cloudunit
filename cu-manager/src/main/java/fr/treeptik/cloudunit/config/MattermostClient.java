package fr.treeptik.cloudunit.config;

import fr.treeptik.mattermost.Client;
import fr.treeptik.mattermost.DefaultClient;
import fr.treeptik.mattermost.api.post.PostTemplate;
import fr.treeptik.mattermost.api.user.UserTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

public class MattermostClient {

    private Logger logger = LoggerFactory.getLogger(MattermostClient.class);

    @Value("${mattermost.user.login}")
    private String userLogin;

    @Value("${mattermost.user.password}")
    private String userPassword;

    @Value("${mattermost.endPoint}")
    private String endPoint;

    @Value("${mattermost.port}")
    private Integer port;

    public void addMessage(String team, String channel, String message) {
        logger.debug("userLogin={}", userLogin);
        logger.debug("endPoint={}", endPoint);
        Client client = new DefaultClient(port, endPoint);
        UserTemplate userTemplate = new UserTemplate(client);
        userTemplate.connection(userLogin, userPassword).map(aVoid -> {
            PostTemplate postTemplate = new PostTemplate(client);
            return postTemplate.createPost(team, channel, message).setHandler(
                    event -> logger.debug(event.result().toString())
            );
        });
    }

}
