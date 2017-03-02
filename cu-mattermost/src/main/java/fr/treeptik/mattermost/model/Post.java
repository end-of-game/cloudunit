package fr.treeptik.mattermost.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by nicolas on 22/02/2017.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Post {

  @JsonProperty("id")
  private String id;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  @JsonProperty("user_id")
  private String userId;

  @JsonProperty("channel_id")
  private String channelId;

  @JsonProperty("root_id")
  private String rootId;

  @JsonProperty("parent_id")
  private String parentId;

  @JsonProperty("original_id")
  private String originalId;

  @JsonProperty("message")
  private String message;

  @JsonProperty("type")
  private String type;

  @JsonProperty("hash_tags")
  private String hashTags;

  @JsonProperty("pending_post_id")
  private String pendingPostId;

  public Post() {
  }

  public Post(String message) {
    this.message = message;
  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public String getChannelId() {
    return channelId;
  }

  public void setChannelId(String channelId) {
    this.channelId = channelId;
  }

  public String getRootId() {
    return rootId;
  }

  public void setRootId(String rootId) {
    this.rootId = rootId;
  }

  public String getParentId() {
    return parentId;
  }

  public void setParentId(String parentId) {
    this.parentId = parentId;
  }

  public String getOriginalId() {
    return originalId;
  }

  public void setOriginalId(String originalId) {
    this.originalId = originalId;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getHashTags() {
    return hashTags;
  }

  public void setHashTags(String hashTags) {
    this.hashTags = hashTags;
  }

  public String getPendingPostId() {
    return pendingPostId;
  }

  public void setPendingPostId(String pendingPostId) {
    this.pendingPostId = pendingPostId;
  }

  @Override
  public String toString() {
    return "Post{" +
      "id='" + id + '\'' +
      ", message='" + message + '\'' +
      '}';
  }
}
