package fr.treeptik.mattermost;

import fr.treeptik.mattermost.model.*;
import io.vertx.core.*;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.*;
import io.vertx.core.json.Json;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import rx.Observable;

/**
 * Created by nicolas on 08/01/2017.
 */
public class DefaultClient implements Client {

  private final Logger logger = LoggerFactory.getLogger(DefaultClient.class);

  private HttpClient httpClient;
  private String token;

  @Override
  public String getToken() {
    return token;
  }

  @Override
  public void setToken(String id) {
    this.token = id;
  }

  @Override
  public String get_API_URI() {
    return "/api/v3";
  }

  public DefaultClient(Integer port, String host) {
    HttpClientOptions options = new HttpClientOptions();
    options.setDefaultHost(host);
    options.setDefaultPort(port);
    options.setProtocolVersion(HttpVersion.HTTP_1_1);
    httpClient = Vertx.vertx().createHttpClient(options);
  }

  /**
   * Connection to remote instance
   *
   * @param login
   * @param password
   * @return
   */
  // Misc operations
  @Override
  public Future<User> connection(final String login, final String password) {
    String apiUri = get_API_URI() + "/users/login";
    logger.info("MatterMost API uri : " + apiUri);
    Future future = Future.future();
    String payload = Json.encode(new Login(login, password));
    Buffer buffer = Buffer.buffer(payload.getBytes());
    HttpClientRequest request = httpClient.post(apiUri, new Handler<HttpClientResponse>() {
      @Override
      public void handle(HttpClientResponse response) {
        response.handler(json -> {
          if (response.statusCode() == 200) {
            final User user = Json.decodeValue(json.toString(), User.class);
            String token = response.headers().get("Token");
            user.setToken(token);
            future.complete(user);
          } else {
            future.fail(response.statusMessage());
          }
        });
      }
    });
    request.exceptionHandler(e -> {
      future.fail(e.getMessage());
    });
    request.putHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(buffer.length()));
    request.putHeader(HttpHeaders.CONTENT_TYPE, "application/json");
    request.end(buffer);
    return future;
  }

  /**
   * Get the channel by name
   *
   * @param token
   * @param teamId
   * @param name
   * @return
   */
  public Future<Channel> getChannelByName(final String token, final String teamId, final String name) {
    String apiUri = get_API_URI() + "/teams/"+teamId+"/channels/name/"+name;
    logger.info("MatterMost API uri : " + apiUri);
    Future future = Future.future();
    HttpClientRequest request = httpClient.get(apiUri, new Handler<HttpClientResponse>() {
      @Override
      public void handle(HttpClientResponse response) {
        response.handler(json -> {
          if (response.statusCode() == 200) {
            final Channel channel = Json.decodeValue(json.toString(), Channel.class);
            future.complete(channel);
          } else {
            future.fail(response.statusMessage());
          }
        });
      }
    });
    request.exceptionHandler(e -> {
      future.fail(e.getMessage());
    });
    request.putHeader("Authorization","Bearer " + token);
    request.end();
    return future;
  }

  /**
   * Get the team by Name
   *
   * @param name
   * @return
   */
  public Future<Team> getTeamByName(final String name) {
    String apiUri = get_API_URI() + "/teams/name/"+name;
    logger.info("MatterMost API uri : " + apiUri);
    Future future = Future.future();
    HttpClientRequest request = httpClient.get(apiUri, new Handler<HttpClientResponse>() {
      @Override
      public void handle(HttpClientResponse response) {
        response.handler(json -> {
          if (response.statusCode() == 200) {
            final Team team = Json.decodeValue(json.toString(), Team.class);
            future.complete(team);
          } else {
            future.fail(response.statusMessage());
          }
        });
      }
    });
    request.exceptionHandler(e -> {
      future.fail(e.getMessage());
    });
    request.end();
    return future;
  }

  /**
   * Create a new Post
   *
   * @param token
   * @param teamId
   * @param channelId
   * @param post
   * @return
   */
  public Future<Post> createPost(final String token, final String teamId, String channelId, Post post) {
    post.setChannelId(channelId);
    String payload = Json.encode(post);
    Buffer buffer = Buffer.buffer(payload.getBytes());
    String apiUri = get_API_URI() + "/teams/"+teamId+"/channels/"+channelId+"/posts/create";
    logger.info("MatterMost API uri : " + apiUri);
    Future<Post> future = Future.future();
    HttpClientRequest request = httpClient.post(apiUri, new Handler<HttpClientResponse>() {
      @Override
      public void handle(HttpClientResponse response) {
        response.handler(json -> {
          if (response.statusCode() == 200) {
            final Post post = Json.decodeValue(json.toString(), Post.class);
            future.complete(post);
          } else {
            future.fail(response.statusMessage());
          }
        });
      }
    });
    request.exceptionHandler(e -> {
      future.fail(e.getMessage());
    });
    request.putHeader("Authorization","Bearer " + token);
    request.putHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(buffer.length()));
    request.putHeader(HttpHeaders.CONTENT_TYPE, "application/json");
    request.end(buffer);
    return future;
  }


}
