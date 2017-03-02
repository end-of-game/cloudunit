package fr.treeptik.mattermost.api.post;

import fr.treeptik.mattermost.Client;
import fr.treeptik.mattermost.model.Post;
import io.vertx.core.Future;

/**
 * Created by nicolas on 24/02/2017.
 */
public class PostTemplate implements PostOperations {

  private Client client;

  public PostTemplate(Client client) {
    this.client = client;
  }

  @Override
  public Future<Post> createPost(String teamName, String channelName, String message) {
    return client.getTeamByName("cloudunit")
      .compose(team -> client.getChannelByName(client.getToken(), team.getId(), "application")
        .compose(channel -> {
          Post request = new Post(message);
          return client.createPost(client.getToken(), team.getId(), channel.getId(), request);
        })
      );
  }

}
