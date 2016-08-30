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

import java.io.Serializable;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Image implements Serializable {

	public final static Integer DISABLED = 0;

	public final static Integer ENABLED = 1;

	public final static String MODULE = "module";

	public final static String SERVER = "server";

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	private String name;

	private String path;

	private String displayName;

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	private Integer status;

	private String imageType;

	private String managerName;

	private String prefixEnv;

	private Integer prefixId;

	@JsonIgnore
	@OneToMany(mappedBy = "image")
	private List<Module> modules;

	@JsonIgnore
	@OneToMany(mappedBy = "image")
	private List<Server> servers;

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

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public List<Module> getModules() {
		return modules;
	}

	public void setModules(List<Module> modules) {
		this.modules = modules;
	}

	public List<Server> getServers() {
		return servers;
	}

	public void setServers(List<Server> servers) {
		this.servers = servers;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getImageType() {
		return imageType;
	}

	public void setImageType(String imageType) {
		this.imageType = imageType;
	}

	public String getManagerName() {
		return managerName;
	}

	public void setManagerName(String managerName) {
		this.managerName = managerName;
	}

	@Override
	public String toString() {
		return "Image [id=" + id + ", name=" + name + ", path=" + path + ", status=" + status + ", imageType="
				+ imageType + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		Image other = (Image) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	public String getPrefixEnv() {
		return prefixEnv;
	}

	public void setPrefixEnv(String prefixEnv) {
		this.prefixEnv = prefixEnv;
	}

	// do not remove prefixId use for splitting server by nature
	public Integer getPrefixId() {
		return prefixEnv.hashCode();
	}


}
