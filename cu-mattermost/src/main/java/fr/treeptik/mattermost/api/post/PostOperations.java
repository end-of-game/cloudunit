package fr.treeptik.mattermost.api.post;

import fr.treeptik.mattermost.model.Channel;
import fr.treeptik.mattermost.model.Post;
import fr.treeptik.mattermost.model.User;
import io.vertx.core.Future;

/**
 * Created by nicolas on 21/02/2017.
 */
public interface PostOperations {

  Future<Post> createPost(String teamName, String channelName, String message);

}
