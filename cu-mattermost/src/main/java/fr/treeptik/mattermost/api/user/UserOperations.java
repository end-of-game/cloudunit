package fr.treeptik.mattermost.api.user;

import fr.treeptik.mattermost.model.User;
import io.vertx.core.Future;
import rx.Observable;

/**
 * Created by nicolas on 21/02/2017.
 */
public interface UserOperations {

  Future<Void> connection(final String login, final String password);

}
