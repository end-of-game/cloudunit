package fr.treeptik.mattermost;

import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(VertxUnitRunner.class)
public class TeamOperationTest {

  @Test
  public void testGetTeamConnection(TestContext tc) {
    Async async = tc.async();
    Client client = new DefaultClient(8065, "localhost");
    client.connection("nicolas", "mypassword")
      .compose(user -> client.getTeamByName("cloudunit"))
      .setHandler(tc.asyncAssertSuccess(team -> {
        System.out.println(team);
        async.complete();
      })
    );

  }


}
