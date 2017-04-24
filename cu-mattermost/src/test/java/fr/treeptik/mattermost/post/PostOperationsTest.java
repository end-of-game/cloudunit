package fr.treeptik.mattermost.post;

import fr.treeptik.mattermost.Client;
import fr.treeptik.mattermost.DefaultClient;
import fr.treeptik.mattermost.model.Post;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(VertxUnitRunner.class)
public class PostOperationsTest {

  @Test
  public void testPostCreation(TestContext tc) {
    Async async = tc.async();

    Client client = new DefaultClient(8065, "localhost");
    client.connection("nicolas", "mypassword")
      .compose(user -> {
          return client.getTeamByName("cloudunit")
            .compose(team -> client.getChannelByName(user.getToken(), team.getId(), "application")
              .compose(channel -> {
                Post request = new Post("Hello World!");
                return client.createPost(user.getToken(), team.getId(), channel.getId(), request);
              })
            );
        }).setHandler(tc.asyncAssertSuccess(post1 -> {
          System.out.println(post1.toString());
          tc.assertNotNull(post1.getId());
          async.complete();
       }
    ));

  }


}
