/*
 * LICENCE : CloudUnit is available under the Gnu Public License GPL V3 : https://www.gnu.org/licenses/gpl.txt
 *     but CloudUnit is licensed too under a standard commercial license.
 *     Please contact our sales team if you would like to discuss the specifics of our Enterprise license.
 *     If you are not sure whether the GPL is right for you,
 *     you can always test our software under the GPL and inspect the source code before you contact us
 *     about purchasing a commercial license.
 *
 *     LEGAL TERMS : "CloudUnit" is a registered trademark of Treeptik and can't be used to endorse
 *     or promote products derived from this project without prior written permission from Treeptik.
 *     Products or services derived from this software may not be called "CloudUnit"
 *     nor may "Treeptik" or similar confusing terms appear in their names without prior written permission.
 *     For any questions, contact us : contact@treeptik.fr
 */

package fr.treeptik.cloudunit.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Entity
public class Snapshot
    implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private String tag;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String applicationName;

    private String type;

    private String jvmRelease;

    private String jvmOptions;

    private Long jvmMemory;

    private String deploymentStatus;

    @ManyToOne
    private User user;

    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    @ElementCollection
    @JsonIgnore
    private Map<String, ModuleConfiguration> appConfig;

    @ElementCollection
    @JsonIgnore
    private List<String> images;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        if (tag != null) {
            tag = tag.toLowerCase();
        }
        this.tag = tag;
    }

    @JsonIgnore
    public String getUniqueTagName() {
        return user.getLogin() + "-" + tag;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Map<String, ModuleConfiguration> getAppConfig() {
        return appConfig;
    }

    public void setAppConfig(Map<String, ModuleConfiguration> appConfig) {
        this.appConfig = appConfig;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getJvmRelease() {
        return jvmRelease;
    }

    public void setJvmRelease(String jvmRelease) {
        this.jvmRelease = jvmRelease;
    }

    public String getJvmOptions() {
        return jvmOptions;
    }

    public void setJvmOptions(String jvmOptions) {
        this.jvmOptions = jvmOptions;
    }

    public Long getJvmMemory() {
        return jvmMemory;
    }

    public void setJvmMemory(Long jvmMemory) {
        this.jvmMemory = jvmMemory;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDeploymentStatus() {
        return deploymentStatus;
    }

    public void setDeploymentStatus(String deploymentStatus) {
        this.deploymentStatus = deploymentStatus;
    }

    @Override
    public String toString() {
        return "Snapshot{" +
            "id=" + id +
            ", tag='" + tag + '\'' +
            ", applicationName='" + applicationName + '\'' +
            ", type='" + type + '\'' +
            ", jvmRelease='" + jvmRelease + '\'' +
            ", jvmOptions='" + jvmOptions + '\'' +
            ", jvmMemory=" + jvmMemory +
            ", deploymentStatus='" + deploymentStatus + '\'' +
            ", user=" + user +
            ", date=" + date +
            ", appConfig=" + appConfig +
            ", images=" + images +
            '}';
    }

}
