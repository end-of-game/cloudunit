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

import org.apache.commons.lang.time.FastDateFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

@Entity
public class Message implements Serializable {

	private static FastDateFormat DATE_FORMATER = FastDateFormat.getInstance(
			"dd-MM-yyyy HH:mm:ss", TimeZone.getDefault(), Locale.getDefault());

	public static final String INFO = "INFO";

	public static final String ERROR = "ERROR";

	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@Temporal(TemporalType.TIMESTAMP)
	private Date date;

	@ManyToOne
	private User author;

	private String type;

	private String applicationName;

	@Column(columnDefinition = "text")
	private String event;

	private String action;

	public Message() {
		this.date = new Date();
	}

	@Override
	public String toString() {
		return "Message{" + "id=" + id + ", date=" + date + ", author="
				+ author + ", type='" + type + '\'' + ", applicationName='"
				+ applicationName + '\'' + ", event='" + event + '\''
				+ ", action='" + action + '\'' + '}';
	}

	public String getAction() {
		return "application.create";
	}

	public void setAction(String action) {
		this.action = action;
	}

	public Message(Type type) {
		this.date = new Date();

		this.type = type.name();
	}

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

	public String getDateAsString() {
		return DATE_FORMATER.format(date);
	}

	public String getEvent() {
		return event;
	}

	public void setEvent(String event) {
		this.event = event;
	}

	public User getAuthor() {
		return author;
	}

	public void setAuthor(User author) {
		this.author = author;
	}

	public String getApplicationName() {
		return applicationName;
	}

	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
