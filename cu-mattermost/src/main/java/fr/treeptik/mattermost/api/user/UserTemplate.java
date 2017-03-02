package fr.treeptik.mattermost.api.user;

import fr.treeptik.mattermost.Client;
import io.vertx.core.Future;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

/**
 * Created by nicolas on 24/02/2017.
 */
public class UserTemplate implements UserOperations {

  private final Logger logger = LoggerFactory.getLogger(UserTemplate.class);

  private Client client;

  public UserTemplate(Client client) {
    this.client = client;
  }

  @Override
  public Future<Void> connection(final String login, final String password) {
    return client.connection(login, password)
      .map(user -> {
        client.setToken(user.getToken());
        return null;
      });
  }

}
