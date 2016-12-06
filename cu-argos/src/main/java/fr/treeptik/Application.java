package fr.treeptik;

import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.EventStream;
import com.spotify.docker.client.messages.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by nicolas on 13/10/2016.
 */
public class Application {

    private final static Logger logger = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        try {
            final DefaultDockerClient.Builder builder = DefaultDockerClient.fromEnv();
            DefaultDockerClient dockerClient = builder.build();
            final EventStream eventStream = dockerClient.events();
            while(eventStream.hasNext()) {
                final Event event = eventStream.next();
                logger.info(event.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


