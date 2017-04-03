package fr.treeptik.mattermost.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by nicolas on 21/02/2017.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class User {

  private String token;

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  @JsonProperty("username")
  private String username;

  @JsonProperty("auth_service")
  private String authService;

  @JsonProperty("email")
  private String email;

  @JsonProperty("email_verified")
  private boolean emailVerified;

  @JsonProperty("nickname")
  private String nickname;

  @JsonProperty("first_name")
  private String firstName;

  @JsonProperty("last_name")
  private String lastName;

  @JsonProperty("last_activity_at")
  private long lastActivityAt;

  @JsonProperty("last_ping_at")
  private long lastPingAt;

  @JsonProperty("allow_marketing")
  private boolean allowMarketing;

  @JsonProperty("last_password_update")
  private long lastPasswordUpdatedAt;

  @JsonProperty("last_picture_update")
  private long lastPictureUpdatedAt;

  @JsonProperty("locale")
  private String locale;

  @JsonProperty("roles")
  private String roles;

  public User() {
  }

  public String getUsername() {
    return username;
  }

  public String getAuthService() {
    return authService;
  }

  public String getEmail() {
    return email;
  }

  public boolean isEmailVerified() {
    return emailVerified;
  }

  public String getNickname() {
    return nickname;
  }

  public String getFirstName() {
    return firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public long getLastActivityAt() {
    return lastActivityAt;
  }

  public long getLastPingAt() {
    return lastPingAt;
  }

  public boolean isAllowMarketing() {
    return allowMarketing;
  }

  public long getLastPasswordUpdatedAt() {
    return lastPasswordUpdatedAt;
  }

  public long getLastPictureUpdatedAt() {
    return lastPictureUpdatedAt;
  }

  public String getLocale() {
    return locale;
  }

  public String getRoles() {
    return roles;
  }

  @Override
  public String toString() {
    return "User{" +
      "username='" + username + '\'' +
      ", email='" + email + '\'' +
      '}';
  }
}
