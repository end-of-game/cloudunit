package fr.treeptik.mattermost.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by nicolas on 22/02/2017.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Channel {

  @JsonProperty("id")
  private String id;

  @JsonProperty("type")
  private String type;

  @JsonProperty("display_name")
  private String displayName;

  @JsonProperty("name")
  private String name;

  @JsonProperty("header")
  private String header;

  @JsonProperty("purpose")
  private String purpose;

  @JsonProperty("last_post_at")
  private long lastPostAt;

  @JsonProperty("total_msg_count")
  private long totalMsgCount;

  @JsonProperty("extra_update_at")
  private long extraUpdateAt;

  @JsonProperty("creator_id")
  private String creatorId;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

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

  public String getHeader() {
    return header;
  }

  public void setHeader(String header) {
    this.header = header;
  }

  public String getPurpose() {
    return purpose;
  }

  public void setPurpose(String purpose) {
    this.purpose = purpose;
  }

  public long getLastPostAt() {
    return lastPostAt;
  }

  public void setLastPostAt(long lastPostAt) {
    this.lastPostAt = lastPostAt;
  }

  public long getTotalMsgCount() {
    return totalMsgCount;
  }

  public void setTotalMsgCount(long totalMsgCount) {
    this.totalMsgCount = totalMsgCount;
  }

  public long getExtraUpdateAt() {
    return extraUpdateAt;
  }

  public void setExtraUpdateAt(long extraUpdateAt) {
    this.extraUpdateAt = extraUpdateAt;
  }

  public String getCreatorId() {
    return creatorId;
  }

  public void setCreatorId(String creatorId) {
    this.creatorId = creatorId;
  }

  @Override
  public String toString() {
    return "Channel{" +
      "id='" + id + '\'' +
      ", type='" + type + '\'' +
      ", displayName='" + displayName + '\'' +
      ", name='" + name + '\'' +
      '}';
  }
}

