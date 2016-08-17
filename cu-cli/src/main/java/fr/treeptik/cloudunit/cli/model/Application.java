/*
 * LICENCE : CloudUnit is available under the GNU Affero General Public License : https://gnu.org/licenses/agpl.html
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

package fr.treeptik.cloudunit.cli.model;

import fr.treeptik.cloudunit.cli.rest.JsonDateDeserializer;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonDeserialize;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class Application
        implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    private String name;

    private String displayName;

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    /**
     * CloudUnit instance name (e.g. DEV, QA, PROD).
     */
    private String cuInstanceName;

    /**
     * Origin property issue from snapshot when created by clone process.
     */
    private String origin;

    private Status status;

    private String location;

    @JsonDeserialize(using = JsonDateDeserializer.class)
    private Date date;

    private User user;

    private List<Module> modules;

    @JsonIgnore
    private List<Deployment> deployments;

    private List<String> aliases;

    private List<Server> servers;

    private String gitAddress;

    private String domainName;

    private String deploymentStatus;

    /**
     * Suffixe du domaine des applications déployées Ce champ est dynamique à travers le profil maven
     */
    private String suffixCloudUnitIO;

    private Set<PortToOpen> portsToOpen;

    public Set<PortToOpen> getPortsToOpen() {
        return portsToOpen;
    }

    public void setPortsToOpen(Set<PortToOpen> portsToOpen) {
        this.portsToOpen = portsToOpen;
    }

    /**
     * prefixe du domaine de l'url de phpMyAdmin Ce champ est dynamique à travers le profil maven
     */
    private String prefixCloudUnitPhpMyAdmin;

    private String managerIp;

    private String managerPort;

    private String managerHost;

    private String jvmRelease;

    private boolean isAClone;

    public Application() {
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

    public String getCuInstanceName() {
        return cuInstanceName;
    }

    public void setCuInstanceName(String cuInstanceName) {
        this.cuInstanceName = cuInstanceName;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    /**
     * @return the String representation of the enum's value
     */
    public String getStatus() {
        return status.name();
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
        return modules;
    }

    public void setModules(List<Module> modules) {
        this.modules = modules;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public List<Server> getServers() {
        return servers;
    }

    public void setServers(List<Server> servers) {
        this.servers = servers;
    }

    public List<Deployment> getDeployments() {
        return deployments;
    }

    public void setDeployments(List<Deployment> deployments) {
        this.deployments = deployments;
    }

    public String getGitAddress() {
        return gitAddress;
    }

    public void setGitAddress(String gitAddress) {
        this.gitAddress = gitAddress;
    }

    public String getManagerHost() {
        return managerHost;
    }

    public void setManagerHost(String managerHost) {
        this.managerHost = managerHost;
    }

    public void setManagerHost(String managerIP, String managerPort) {
        this.managerHost = managerIP + ":" + managerPort;
    }

    public String getSuffixCloudUnitIO() {
        return suffixCloudUnitIO;
    }

    public void setSuffixCloudUnitIO(String suffixCloudUnitIO) {
        this.suffixCloudUnitIO = suffixCloudUnitIO;
    }

    public String getPrefixCloudUnitPhpMyAdmin() {
        return prefixCloudUnitPhpMyAdmin;
    }

    public void setPrefixCloudUnitPhpMyAdmin(String prefixCloudUnitPhpMyAdmin) {
        this.prefixCloudUnitPhpMyAdmin = prefixCloudUnitPhpMyAdmin;
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public List<String> getAliases() {
        return aliases;
    }

    public void setAliases(List<String> aliases) {
        this.aliases = aliases;
    }

    public String getJvmRelease() {
        return jvmRelease;
    }

    public void setJvmRelease(String jvmRelease) {
        this.jvmRelease = jvmRelease;
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


    @Override
    public String toString() {
        return "Application [id=" + id + ", name=" + name + ", date=" + date + ", user=" + user + "]";
    }

}
