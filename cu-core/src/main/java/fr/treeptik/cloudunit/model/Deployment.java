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
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Deployment implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Integer id;

	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern = "YYYY-MM-dd HH:mm")
	private Date date;

	@JsonIgnore
	@ManyToOne
	private Application application;

	private Type type;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Application getApplication() {
		return application;
	}

	public void setApplication(Application application) {
		this.application = application;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

}
