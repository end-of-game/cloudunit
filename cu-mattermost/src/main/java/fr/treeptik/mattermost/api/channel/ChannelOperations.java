package fr.treeptik.mattermost.api.channel;

import fr.treeptik.mattermost.model.Channel;
import io.vertx.core.Future;

/**
 * Created by nicolas on 21/02/2017.
 */
public interface ChannelOperations {

  Future<Channel> getChannelByName(final String teamName, final String name);

}
