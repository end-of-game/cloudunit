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
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import fr.treeptik.cloudunit.utils.AlphaNumericsCharactersCheckUtils;
import fr.treeptik.cloudunit.utils.NamingUtils;

@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "user_id", "name", "cuInstanceName" }))
public class Application implements Serializable {
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
	private List<Module> modules;

	@OneToOne(mappedBy = "application", fetch = FetchType.LAZY)
	private Server server;

	@OneToMany(mappedBy = "application", fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
	private List<Deployment> deployments;

	@Enumerated(EnumType.STRING)
	private DeploymentStatus deploymentStatus;

	private String contextPath;

	@OneToMany(cascade = CascadeType.REMOVE, mappedBy = "application")
	private List<EnvironmentVariable> environmentVariables;

	protected Application() {}

	private Application(Builder builder) {
        this.name = builder.name;
        this.displayName = builder.displayName;
        this.cuInstanceName = builder.cuInstanceName;
        this.status = builder.status;
        this.user = builder.user;
        this.environmentVariables = builder.environmentVariables;
        this.modules = new ArrayList<>();
        this.deployments = new ArrayList<>();
        
        this.server = new Server(this, builder.image);

		if ("webserver".equalsIgnoreCase(server.getImage().getImageSubType().toString())) {
			this.deploymentStatus = DeploymentStatus.DEPLOYED;
		} else {
			this.deploymentStatus = DeploymentStatus.NONE;
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

    public DeploymentStatus getDeploymentStatus() {
        return deploymentStatus;
    }

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public List<Module> getModules() {
	    return Collections.unmodifiableList(modules);
	}
	
	public boolean hasModule(String name) {
	    return modules.stream()
	            .filter(m -> m.getImage().getName().equals(name))
	            .findAny()
	            .isPresent();
	}
	
	public Module getModule(String name) {
	    return modules.stream()
	            .filter(m -> m.getImage().getName().equals(name))
	            .findAny()
	            .get();
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
	
	public List<Container> getContainers() {
	    List<Container> result = new ArrayList<>();
	    result.add(server);
	    result.addAll(modules);
	    return Collections.unmodifiableList(result);
	}
	
	public boolean hasContainer(String containerId) {
	    return getContainers().stream()
                .filter(c -> c.getContainerID().equals(containerId))
                .findAny()
                .isPresent();
	}
	
	public Container getContainer(String containerId) {
	    return getContainers().stream()
	            .filter(c -> c.getContainerID().equals(containerId))
	            .findAny()
	            .get();
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public List<Deployment> getDeployments() {
	    return Collections.unmodifiableList(deployments);
	}

	public void addDeployment(Deployment deployment) {
	    deployments.add(deployment);
	    
	    deploymentStatus = DeploymentStatus.DEPLOYED;
        contextPath = deployment.getContextPath();
	}
	
    public String getLocation(){
        String domain = NamingUtils.getCloudUnitDomain(System.getenv("CU_DOMAIN"));
        return NamingUtils.getContainerName(name, null, user.getLogin()) + domain;
    }

    public String getContextPath() {
        return contextPath;
    }

	@Override
	public String toString() {
	    return new ToStringBuilder(this)
	            .append("id", id)
	            .append("name", name)
	            .append("status", status)
	            .append("date", date)
	            .toString();
	}

	@Override
	public int hashCode() {
	    return new HashCodeBuilder()
	            .append(user)
	            .append(name)
	            .toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof Application)) return false;
		
		Application other = (Application) obj;
		
		return new EqualsBuilder()
		        .append(this.user, other.user)
		        .append(this.name, other.name)
		        .isEquals();
	}

    public static final class Builder {
        private final String name;
        private String displayName;
        private String cuInstanceName;
        private Status status;
        private User user;
        private List<EnvironmentVariable> environmentVariables = new ArrayList<>();
        private Image image;

        private Builder(String displayName, Image image) {
            this.displayName = displayName;
            this.image = image;
            this.name = AlphaNumericsCharactersCheckUtils.convertToAlphaNumerics(displayName).toLowerCase();
            this.status = Status.PENDING;
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

        public Builder addEnvironmentVariable(EnvironmentVariable environmentVariable) {
            this.environmentVariables.add(environmentVariable);
            return this;
        }

        public Application build() {
            return new Application(this);
        }

    }
}
