package fr.treeptik.cloudunit.model;

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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import fr.treeptik.cloudunit.utils.AlphaNumericsCharactersCheckUtils;
import fr.treeptik.cloudunit.utils.NamingUtils;

@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "user_id", "name", "cuInstanceName" }))
public class Application implements Serializable {

	public static final String ALREADY_DEPLOYED = "ALREADY_DEPLOYED";

	public static final String NONE = "NONE";

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	private String name;

	private String displayName;

    /**
     * CloudUnit instance name (e.g. DEV, QA, PROD).
     */
    private String cuInstanceName;

	@Enumerated(EnumType.STRING)
	private Status status;

	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern = "YYYY-MM-dd HH:mm")
	private Date date;

	@ManyToOne
	private User user;

	@OrderBy("id asc")
	@OneToMany(mappedBy = "application", fetch = FetchType.LAZY)
	private Set<Module> modules;

	@OneToOne(mappedBy = "application", fetch = FetchType.LAZY)
	private Server server;

	@OneToMany(mappedBy = "application", fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
	private Set<Deployment> deployments;

	@ElementCollection
	private Set<String> aliases;

	private String deploymentStatus;

	public String getLocation(){
		String domain = NamingUtils.getCloudUnitDomain(System.getenv("CU_DOMAIN"));
        return NamingUtils.getContainerName(name, null, user.getLogin()) + domain;
	}

	public String getContextPath() {
		return contextPath;
	}

	public void setContextPath(String contextPath) {
		this.contextPath = contextPath;
	}

	private String contextPath;

	@OneToMany(cascade = CascadeType.REMOVE, mappedBy = "application")
	@OrderBy(value = "port")
	private Set<PortToOpen> portsToOpen;

	@OneToMany(cascade = CascadeType.REMOVE, mappedBy = "application")
	private Set<EnvironmentVariable> environmentVariables;

	public Application() {
		date = new Date();
		// todo : create an enum for deployment status
		deploymentStatus = Application.NONE;
		status = Status.PENDING;
	}

	private Application(Builder builder) {
	    this();

        this.name = builder.name;
        this.displayName = builder.displayName;
        this.cuInstanceName = builder.cuInstanceName;
        this.status = builder.status;
        this.user = builder.user;
        this.deploymentStatus = builder.deploymentStatus;
        this.contextPath = builder.contextPath;
        this.environmentVariables = builder.environmentVariables;
        this.portsToOpen = builder.portsToOpen;

        this.modules = new HashSet<>();
        this.deployments = new HashSet<>();
        this.aliases = new HashSet<>();
        
        this.server = new Server(this, builder.image);

		if ("webserver".equalsIgnoreCase(server.getImage().getImageSubType().toString())) {
			this.deploymentStatus = Application.ALREADY_DEPLOYED;
		} else {
			this.deploymentStatus = Application.NONE;
		}

	}

	public static Builder of(String displayName, Image image) {
        return new Builder(displayName, image);
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

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

    public String getCuInstanceName() {
        return cuInstanceName;
    }

    public void setCuInstanceName(String cuInstanceName) {
        this.cuInstanceName = cuInstanceName;
    }

	public Server getServer() {
		return server;
	}

	public void setServer(Server server) {
		this.server = server;
	}

	public void removeServer() {
		server.setApplication(null);
		this.server = null;
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
	
	public Module addModule(Image image) {
	    Module module = new Module(this, image);
	    modules.add(module);
	    
	    return module;
	}

	public void removeModule(Module module) {
		module.setApplication(null);
		modules.remove(module);
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
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

	public void addDeployment(Deployment deployment) { this.deployments.add(deployment); }

	public Set<String> getAliases() {
		return aliases;
	}

	public void setAliases(Set<String> aliases) {
		this.aliases = aliases;
	}

	@Override
	public String toString() {
		return "Application{" + "id=" + id + ", name='" + name + '\'' + ", status=" + status + ", date=" + date + "}'";
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

	public String getDeploymentStatus() {
		return deploymentStatus;
	}

	public void setDeploymentStatus(String deploymentStatus) {
		this.deploymentStatus = deploymentStatus;
	}

	public Set<PortToOpen> getPortsToOpen() {

		if (portsToOpen == null) {
			return new HashSet<>();
		}

		return this.portsToOpen;
	}

    public static final class Builder {

        private final String name;
        private String displayName;
        private String cuInstanceName;
        private Status status;
        private User user;
        private String restHost;
        private String deploymentStatus;
        private String contextPath;
        private Set<PortToOpen> portsToOpen;
        private Set<EnvironmentVariable> environmentVariables;
        private Image image;

        private Builder(String displayName, Image image) {
            this.displayName = displayName;
            this.image = image;
            this.name = AlphaNumericsCharactersCheckUtils.convertToAlphaNumerics(displayName).toLowerCase();
        }

        public Builder withDisplayName(String displayName) {
            this.displayName = displayName;
            return this;
        }

        public Builder withCuInstanceName(String cuInstanceName) {
            this.cuInstanceName = cuInstanceName;
            return this;
        }

        public Builder withStatus(Status status) {
            this.status = status;
            return this;
        }

        public Builder withUser(User user) {
            this.user = user;
            return this;
        }

        public Builder withRestHost(String restHost) {
            this.restHost = restHost;
            return this;
        }

        public Builder withDeploymentStatus(String deploymentStatus) {
            this.deploymentStatus = deploymentStatus;
            return this;
        }

        public Builder withContextPath(String contextPath) {
            this.contextPath = contextPath;
            return this;
        }

        public Builder withPortsToOpen(Set<PortToOpen> portsToOpen) {
            this.portsToOpen = portsToOpen;
            return this;
        }

        public Builder withEnvironmentVariables(Set<EnvironmentVariable> environmentVariables) {
            this.environmentVariables = environmentVariables;
            return this;
        }

        public Application build() {
            return new Application(this);
        }

    }
}
