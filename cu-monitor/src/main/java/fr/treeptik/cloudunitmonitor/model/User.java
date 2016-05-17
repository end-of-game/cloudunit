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

package fr.treeptik.cloudunitmonitor.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class User
    implements Serializable
{

    public static final Integer STATUS_MAIL_NOT_CONFIRMED = 0;

    public static final Integer STATUS_ACTIF = 1;

    public static final Integer STATUS_INACTIF = 2;

    public static final Integer STATUS_NOT_ALLOWED = 3;

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue( strategy = GenerationType.AUTO )
    private Integer id;

    @Column( unique = true, nullable = false )
    private String login;

    private String firstName;

    private String lastName;

    private String organization;

    @Temporal( TemporalType.TIMESTAMP )
    private Date signin;

    @Temporal( TemporalType.TIMESTAMP )
    private Date lastConnection;

    @Column( unique = true, nullable = false )
    private String email;

    private String password;

    private Integer status;

    @ManyToOne
    private Role role;

    @OneToMany( mappedBy = "user" )
    private List<Application> applications;

    public User()
    {

    }

    // Constructeur pour administration

    public User( Integer id, String firstName, String lastName, String organization, Date signin, String email,
                 String password, Integer status, Role role, List<Application> applications )
    {
        super();
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.organization = organization;
        this.signin = signin;
        this.email = email;
        this.password = password;
        this.status = status;
        this.role = role;
        this.applications = applications;
    }

    public User( String login, String firstName, String lastName, String organization, String email, String password )
    {
        this.login = login;
        this.firstName = firstName;
        this.lastName = lastName;
        this.organization = organization;
        this.email = email;
        this.password = password;
    }

    public Integer getId()
    {
        return id;
    }

    public void setId( Integer id )
    {
        this.id = id;
    }

    public String getFirstName()
    {
        return firstName;
    }

    public void setFirstName( String firstName )
    {
        this.firstName = firstName;
    }

    public String getLastName()
    {
        return lastName;
    }

    public void setLastName( String lastName )
    {
        this.lastName = lastName;
    }

    public String getOrganization()
    {
        return organization;
    }

    public void setOrganization( String organization )
    {
        this.organization = organization;
    }

    public Date getSignin()
    {
        return signin;
    }

    public void setSignin( Date signin )
    {
        this.signin = signin;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail( String email )
    {
        this.email = email;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword( String password )
    {
        this.password = password;
    }

    public Integer getStatus()
    {
        return status;
    }

    public void setStatus( Integer status )
    {
        this.status = status;
    }

    public Role getRole()
    {
        return role;
    }

    public void setRole( Role role )
    {
        this.role = role;
    }

    public List<Application> getApplications()
    {
        return applications;
    }

    public void setApplications( List<Application> applications )
    {
        this.applications = applications;
    }

    public String getLogin()
    {
        return login;
    }

    public void setLogin( String login )
    {
        this.login = login;
    }

    public Date getLastConnection()
    {
        return lastConnection;
    }

    public void setLastConnection( Date lastConnection )
    {
        this.lastConnection = lastConnection;
    }

    @Override
    public String toString()
    {
        return "User [id=" + id + ", firstName=" + firstName + ", lastName=" + lastName + ", signin=" + signin
            + ", email=" + email + ", password=***" + ", status=" + status + "]";
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ( ( id == null ) ? 0 : id.hashCode() );
        return result;
    }

    @Override
    public boolean equals( Object obj )
    {
        if ( this == obj )
            return true;
        if ( obj == null )
            return false;
        if ( getClass() != obj.getClass() )
            return false;
        User other = (User) obj;
        if ( id == null )
        {
            if ( other.id != null )
                return false;
        }
        else if ( !id.equals( other.id ) )
            return false;
        return true;
    }

}
