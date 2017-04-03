package fr.treeptik.mattermost;

import fr.treeptik.mattermost.model.Channel;
import fr.treeptik.mattermost.model.Post;
import fr.treeptik.mattermost.model.Team;
import fr.treeptik.mattermost.model.User;
import io.vertx.core.Future;

/**
 * Created by nicolas on 08/01/2017.
 */
public interface Client {

  static DefaultClient newClient(final Integer port, final String host) {
    return new DefaultClient(port, host);
  }

  String get_API_URI();
  void setToken(String id);
  String getToken();

  Future<Post> createPost(final String token, final String teamId, String channelId, Post post);
  Future<Channel> getChannelByName(final String token, final String teamId, final String name);
  Future<Team> getTeamByName(final String name);
  Future<User> connection(final String login, final String password);

}
