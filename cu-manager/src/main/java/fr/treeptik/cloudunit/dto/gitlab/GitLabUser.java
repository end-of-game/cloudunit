package fr.treeptik.cloudunit.dto.gitlab;

/**
 * This class represents a Gitlab user
 * Created by angular5 on 29/04/16.
 */
public class GitLabUser {
    private int id;
    private String username;
    private String name;
    private String email;
    private String password;
    private Boolean role;

    public GitLabUser(String username, String name, String email, String password, Boolean role) {
        this.username = username;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) { this.id = id; }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getRole() {
        return role;
    }

    public void setRole(Boolean role) {
        this.role = role;
    }
}
