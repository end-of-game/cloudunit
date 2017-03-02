package fr.treeptik.mattermost.post;

import fr.treeptik.mattermost.Client;
import fr.treeptik.mattermost.DefaultClient;
import fr.treeptik.mattermost.api.post.PostTemplate;
import fr.treeptik.mattermost.api.user.UserTemplate;
import fr.treeptik.mattermost.model.Post;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(VertxUnitRunner.class)
public class PostTemplateTest {

  @Test
  public void testConnection(TestContext tc) {
    Async async = tc.async();
    Client client = new DefaultClient(8065, "localhost");
    UserTemplate userTemplate = new UserTemplate(client);
    userTemplate.connection("nicolas", "mypassword").map(v -> {
      PostTemplate postTemplate = new PostTemplate(client);
      return postTemplate.createPost("cloudunit", "application", "Hello Bill").setHandler(
        event -> {
          tc.assertEquals("Hello Bill", event.result().getMessage());
          async.complete();
        }
      );
    });
  }

}
