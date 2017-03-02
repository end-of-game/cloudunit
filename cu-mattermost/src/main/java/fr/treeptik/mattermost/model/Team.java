package fr.treeptik.mattermost.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by nicolas on 22/02/2017.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Team {

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  @JsonProperty("id")
  private String id;

  @JsonProperty("display_name")
  private String displayName;

  @JsonProperty("name")
  private String name;

  @JsonProperty("email")
  private String email;

  @JsonProperty("type")
  private String type;

  @JsonProperty("company_name")
  private String companyName;

  @JsonProperty("allowed_domains")
  private String allowedDomains;

  @JsonProperty("invite_id")
  private String inviteId;

  @JsonProperty("allow_open_invite")
  private boolean allowOpenInvite;

  @JsonProperty("allow_team_listing")
  private boolean allowTeamListing;

  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getCompanyName() {
    return companyName;
  }

  public void setCompanyName(String companyName) {
    this.companyName = companyName;
  }

  public String getAllowedDomains() {
    return allowedDomains;
  }

  public void setAllowedDomains(String allowedDomains) {
    this.allowedDomains = allowedDomains;
  }

  public String getInviteId() {
    return inviteId;
  }

  public void setInviteId(String inviteId) {
    this.inviteId = inviteId;
  }

  public boolean isAllowOpenInvite() {
    return allowOpenInvite;
  }

  public void setAllowOpenInvite(boolean allowOpenInvite) {
    this.allowOpenInvite = allowOpenInvite;
  }

  public boolean isAllowTeamListing() {
    return allowTeamListing;
  }

  public void setAllowTeamListing(boolean allowTeamListing) {
    this.allowTeamListing = allowTeamListing;
  }

  @Override
  public String toString() {
    return "Team{" +
      "id='" + id + '\'' +
      ", displayName='" + displayName + '\'' +
      ", name='" + name + '\'' +
      '}';
  }
}
