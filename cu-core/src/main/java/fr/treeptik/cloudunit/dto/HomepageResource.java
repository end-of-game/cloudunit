package fr.treeptik.cloudunit.dto;

/**
 * Created by nicolas on 12/12/2016.
 */
public class HomepageResource {
    private String jenkins;
    private String gitlab;
    private String kibana;
    private String nexus;
    private String sonar;
    private String mattermost;
    private String prometheus;

    public HomepageResource() {
    }

    public HomepageResource(String jenkins, String gitlab, String kibana, String nexus, String sonar, String mattermost, String prometheus) {
        this.jenkins = jenkins;
        this.gitlab = gitlab;
        this.kibana = kibana;
        this.nexus = nexus;
        this.sonar = sonar;
        this.mattermost = mattermost;
        this.prometheus = prometheus;
    }

    public String getJenkins() {
        return jenkins;
    }

    public String getGitlab() {
        return gitlab;
    }

    public String getKibana() {
        return kibana;
    }

    public String getNexus() {
        return nexus;
    }

    public String getSonar() {
        return sonar;
    }

    public String getMattermost() {
        return mattermost;
    }
    
    public String getPrometheus() {
        return prometheus;
    }
    
}
