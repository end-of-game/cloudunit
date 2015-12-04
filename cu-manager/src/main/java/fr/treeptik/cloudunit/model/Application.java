/*
 * LICENCE : CloudUnit is available under the GNU Affero General Public License : https://gnu.org/licenses/agpl.html
 * but CloudUnit is licensed too under a standard commercial license.
 * Please contact our sales team if you would like to discuss the specifics of our Enterprise license.
 * If you are not sure whether the AGPL is right for you,
 * you can always test our software under the AGPL and inspect the source code before you contact us
 * about purchasing a commercial license.
 *
 * LEGAL TERMS : "CloudUnit" is a registered trademark of Treeptik and can't be used to endorse
 * or promote products derived from this project without prior written permission from Treeptik.
 * Products or services derived from this software may not be called "CloudUnit"
 * nor may "Treeptik" or similar confusing terms appear in their names without prior written permission.
 * For any questions, contact us : contact@treeptik.fr
 */


package fr.treeptik.cloudunit.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import fr.treeptik.cloudunit.utils.JsonDateSerializer;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "name"}))
public class Application
        implements Serializable {

    public static final String ALREADY_DEPLOYED = "ALREADY_DEPLOYED";

    public static final String NONE = "NONE";

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private String name;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Temporal(TemporalType.TIMESTAMP)
    @JsonSerialize(using = JsonDateSerializer.class)
    private Date date;

    @ManyToOne
    private User user;

    @OrderBy("id asc")
    @OneToMany(mappedBy = "application", fetch = FetchType.LAZY)
    private Set<Module> modules;

    @OrderBy("id asc")
    @OneToMany(mappedBy = "application", fetch = FetchType.LAZY)
    private Set<Server> servers;

    @OneToMany(mappedBy = "application", fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private Set<Deployment> deployments;


    private String gitAddress;

    @ElementCollection
    private Set<String> aliases;

    /**
     * Suffixe du domaine des applications déployées Ce champ est dynamique à
     * travers le profil maven
     */
    private String suffixCloudUnitIO;

    private String domainName;

    private String managerIp;

    private String managerPort;

    // version de java sous laquelle tourne les containers serveurs et git
    // (maven)
    private String jvmRelease;

    @JsonIgnore
    private String restHost;

    @JsonIgnore
    private String gitContainerIP;

    @JsonIgnore
    private String gitSshProxyPort;

    private String deploymentStatus;

    private boolean isAClone;

    @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "application")
    @OrderBy(value = "port")
    private Set<PortToOpen> portsToOpen;

    public Application() {
        super();
        date = new Date();
        isAClone = false;
        deploymentStatus = Application.NONE;
    }

    public Application(Integer id, String name, User user, List<Module> modules) {
        super();
        this.id = id;
        this.name = name;
        this.user = user;
        this.modules = new HashSet<>(modules);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Module> getModules() {
        if (modules != null) {
            return new ArrayList<>(modules);
        } else {
            return new ArrayList<>();
        }
    }

    public void setModules(List<Module> modules) {
        this.modules = new HashSet<>(modules);
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public List<Server> getServers() {
        if (servers != null) {
            return new ArrayList<>(servers);
        } else {
            return new ArrayList<>();
        }
    }

    public void setServers(List<Server> servers) {
        this.servers = new HashSet<>(servers);
    }

    public List<Deployment> getDeployments() {
        if (deployments != null) {
            return new ArrayList<>(deployments);
        } else {
            return new ArrayList<>();
        }
    }

    public void setDeployments(List<Deployment> deployments) {
        this.deployments = new HashSet<>(deployments);
    }

    public String getGitAddress() {
        return gitAddress;
    }

    public void setGitAddress(String gitAddress) {
        this.gitAddress = gitAddress;
    }

    public String getSuffixCloudUnitIO() {
        return suffixCloudUnitIO;
    }

    public void setSuffixCloudUnitIO(String suffixCloudUnitIO) {
        this.suffixCloudUnitIO = suffixCloudUnitIO;
    }

    public String getManagerIp() {
        return managerIp;
    }

    public void setManagerIp(String managerIp) {
        this.managerIp = managerIp;
    }

    public String getManagerPort() {
        return managerPort;
    }

    public void setManagerPort(String managerPort) {
        this.managerPort = managerPort;
    }

    public String getRestHost() {
        return restHost;
    }

    public void setRestHost(String restHost) {
        this.restHost = restHost;
    }

    public String getGitContainerIP() {
        return gitContainerIP;
    }

    public void setGitContainerIP(String gitContainerIP) {
        this.gitContainerIP = gitContainerIP;
    }

    public String getGitSshProxyPort() {
        return gitSshProxyPort;
    }

    public void setGitSshProxyPort(String gitSshProxyPort) {
        this.gitSshProxyPort = gitSshProxyPort;
    }

    public String getLocation() {
        return "http://" + name + "-" + user.getLogin() + "-"
                + user.getOrganization() + suffixCloudUnitIO;
    }

    public Set<String> getAliases() {
        return aliases;
    }

    public void setAliases(Set<String> aliases) {
        this.aliases = aliases;
    }

    public String getJvmRelease() {
        return jvmRelease;
    }

    public void setJvmRelease(String jvmRelease) {
        this.jvmRelease = jvmRelease;
    }

    @Override
    public String toString() {
        return "Application{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", status=" + status +
                ", date=" + date +
                ", user=" + user +
                ", domainName='" + domainName + '\'' +
                ", managerIP='" + managerIp + '\'' +
                ", managerPort='" + managerPort + '\'' +
                ", jvmRelease='" + jvmRelease + '\'' +
                ", restHost='" + restHost + '\'' +
                ", gitContainerIP='" + gitContainerIP + '\'' +
                ", deploymentStatus='" + deploymentStatus + '\'' +
                ", gitSshProxyPort='" + gitSshProxyPort + '\'' +
                ", gitAddress='" + gitAddress + '\'' +
                ", suffixCloudUnitIO='" + suffixCloudUnitIO + '\'' +
                ", isAClone=" + isAClone +
                '}';
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((user == null) ? 0 : user.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Application other = (Application) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (user == null) {
            if (other.user != null)
                return false;
        } else if (!user.equals(other.user))
            return false;
        return true;
    }

    public String getDomainName() {
        return domainName;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    public boolean isAClone() {
        return isAClone;
    }

    public void setAClone(boolean isAClone) {
        this.isAClone = isAClone;
    }

    public String getDeploymentStatus() {
        return deploymentStatus;
    }

    public void setDeploymentStatus(String deploymentStatus) {
        this.deploymentStatus = deploymentStatus;
    }

    /**
     * One application is composed by many containers
     * These containers containers can be servers, modules or tools
     * These methods return the SSH Port associated for the container Id
     *
     * @return
     */
    public String getSShPortByContainerId(String id) {
        if (id == null) {
            return null;
        }
        String sshPort = null;
        for (Server server : servers) {
            if (id.equals(server.getContainerID().substring(0, 12))) {
                sshPort = server.getSshPort();
            }
        }
        // real modules + tools
        for (Module module : modules) {
            if (id.equals(module.getContainerID().substring(0, 12))) {
                sshPort = module.getSshPort();
            }
        }
        return sshPort;
    }

    public Set<PortToOpen> getPortsToOpen() {

        if (portsToOpen == null) {
            return new HashSet<>();
        }

        return this.portsToOpen;
    }

    public void setPortsToOpen(Set<PortToOpen> portsToOpen) {
        this.portsToOpen = portsToOpen;
    }


}
