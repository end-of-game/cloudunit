package fr.treeptik.examples;

import fr.treeptik.mattermost.Client;
import fr.treeptik.mattermost.DefaultClient;
import fr.treeptik.mattermost.api.post.PostTemplate;
import fr.treeptik.mattermost.api.user.UserTemplate;

/**
 * Created by nicolas on 24/02/2017.
 */
public class Simple {

  public static void main(String[] args) {
    Client client = new DefaultClient(8065, "localhost");
    UserTemplate userTemplate = new UserTemplate(client);
    userTemplate.connection("nicolas", "mypassword").map(v -> {
      PostTemplate postTemplate = new PostTemplate(client);
      return postTemplate.createPost("cloudunit", "application", "Hello World!").setHandler(
        event -> System.out.println(event.result().toString())
      );
    });
  }

}
