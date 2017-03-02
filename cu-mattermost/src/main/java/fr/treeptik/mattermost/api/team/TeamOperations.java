package fr.treeptik.mattermost.api.team;

import fr.treeptik.mattermost.model.Team;
import io.vertx.core.Future;

/**
 * Created by nicolas on 21/02/2017.
 */
public interface TeamOperations {

  Future<Team> getTeamByName(final String name);

}
