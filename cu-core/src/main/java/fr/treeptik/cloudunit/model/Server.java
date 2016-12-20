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

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Server extends Container implements Serializable {

	private static final long serialVersionUID = 1L;

    private static final long DEFAULT_JVM_MEMORY = 512L;

	private Long jvmMemory;

	@Column(columnDefinition = "TEXT")
	private String jvmOptions;

	private String managerLocation;

	@JsonIgnore
	@OneToOne(fetch = FetchType.LAZY)
	private Application application;

	@JsonIgnore
	@OneToMany(mappedBy = "volumeAssociationId.server", fetch = FetchType.LAZY)
	private Set<VolumeAssociation> volumeAssociations;

	public Server() {
	}
	
	public Server(Builder builder) {
        super(builder);
        
        this.jvmMemory = builder.jvmMemory;
        this.jvmOptions = builder.jvmOptions;
        this.managerLocation = builder.managerLocation;
    }
	
	public Server(Application application, Image image) {
	    super(application, image);
	    
	    this.jvmOptions = "";
	    this.jvmMemory = DEFAULT_JVM_MEMORY;
	}

    public static abstract class AbstractBuilder<T extends AbstractBuilder<T>> extends Container.AbstractBuilder<T> {
        protected Long jvmMemory;
        protected String jvmOptions;
        protected String jvmRelease;
        protected String managerLocation;
        
        protected AbstractBuilder(Image image) {
            super(image);
        }

	    public T withJvmMemory(Long jvmMemory) {
	        this.jvmMemory = jvmMemory;
	        return self();
	    }
	    
	    public T withJvmOptions(String jvmOptions) {
	        this.jvmOptions = jvmOptions;
	        return self();
	    }
	    
	    public T withJvmRelease(String jvmRelease) {
	        this.jvmRelease = jvmRelease;
	        return self();
	    }
	    
	    public T withManagerLocation(String managerLocation) {
	        this.managerLocation = managerLocation;
	        return self();
	    }
	}
	
	public static final class Builder extends AbstractBuilder<Builder> {
        protected Builder(Image image) {
            super(image);
        }

        @Override
        protected Builder self() {
            return this;
        }
	    
        public Server build() {
            return new Server(this);
        }
	}
	
	public static Builder of(Image image) {
	    return new Builder(image);
	}

	public Long getJvmMemory() {
		return jvmMemory;
	}

	public void setJvmMemory(Long jvmMemory) {
		this.jvmMemory = jvmMemory;
	}

	public String getJvmOptions() {
		return jvmOptions;
	}

	public void setJvmOptions(String opts) {
		this.jvmOptions = opts;
	}

	public String getManagerLocation() {
		return managerLocation;
	}

	public void setManagerLocation(String managerLocation) {
		this.managerLocation = managerLocation;
	}

	public boolean isApplicationServer() {
		return image.getImageSubType().equals("APPSERVER");
	}

	@Override
	public String toString() {
		return "Server [id=" + id + ", startDate=" + startDate + ", name=" + name + ", cloudId=" + containerID
				+ ", memorySize=" + memorySize + ", containerIP=" + containerIP + ", image=" + image + ", status="
				+ status + "]";
	}

	public Application getApplication() {
		return application;
	}

	public void setApplication(Application application) {
		this.application = application;
	}

	public Set<VolumeAssociation> getVolumeAssociations() {
		return volumeAssociations;
	}

	public void setVolumeAssociations(Set<VolumeAssociation> volumeAssociations) {
		this.volumeAssociations = volumeAssociations;
	}

}
