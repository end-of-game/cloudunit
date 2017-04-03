package fr.treeptik.mattermost.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by nicolas on 21/02/2017.
 */
public class Login {

  @JsonProperty("login_id")
  private String login;

  @JsonProperty("password")
  private String password;

  public Login() {
  }

  /**
   * @param login can be nickname, email or ldap id
   * @param password
   */
  public Login(final String login, final String password) {
    this.login = login;
    this.password = password;
  }

  public void setLogin(String login) {
    this.login = login;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getLogin() {
    return login;
  }

  public String getPassword() {
    return password;
  }
}
