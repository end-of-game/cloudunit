package fr.treeptik.cloudunit.model;/*
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

import com.fasterxml.jackson.annotation.JsonIgnore;
import fr.treeptik.cloudunit.model.action.ModuleAction;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.PostLoad;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Entity
public class Module extends Container implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * linkAlias - userName - password - phpMyAdmin - database
	 */
	@ElementCollection
	protected Map<String, String> moduleInfos = new HashMap<>();

	@Transient
	protected String suffixCU;

	@Transient
	@JsonIgnore
	private ModuleAction moduleAction;

	private String managerLocation;

	@ManyToOne
	@JsonIgnore
	private Application application;

	public Module() {
		this.image = new Image();
		this.moduleInfos = new HashMap<>();
	}

	public String getManagerLocation() {
		return managerLocation;
	}

	public void setManagerLocation(String managerLocation) {
		this.managerLocation = managerLocation;
	}

	public Map<String, String> getModuleInfos() {
		return moduleInfos;
	}

	public void setModuleInfos(Map<String, String> moduleInfos) {
		this.moduleInfos = moduleInfos;
	}

	public ModuleAction getModuleAction() {
		return moduleAction;
	}

	public void setModuleAction(ModuleAction moduleAction) {
		this.moduleAction = moduleAction;
	}

	public Application getApplication() {
		return application;
	}

	public void setApplication(Application application) {
		this.application = application;
	}

	@PostLoad
	public void initModuleActionFromJPA() {
		ModuleFactory.updateModule(this);
	}

	@Override
	public String toString() {
		return "Module [id=" + id + ", startDate=" + startDate + ", name=" + name + ", cloudId=" + containerID + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		Module other = (Module) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@JsonIgnore
	public Long getInstanceNumber() {
		if (name == null) {
			throw new RuntimeException("Cannot get instance number without first call initNewModule");
		}
		return Long.parseLong((name.substring(name.lastIndexOf("-") + 1)));
	}

}
