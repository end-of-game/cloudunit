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


import javax.persistence.*;

/**
 * Created by gborg on 09/02/17.
 */
@Entity
public class Registry {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(unique = true, nullable = false)
    private String endpoint;

    private String username;

    private String password;

    private String email;

    public Registry() { }

    public Registry(Integer id, String endpoint, String username, String password, String email) {
        this.id = id;
        this.endpoint = endpoint;
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public Registry(String endpoint, String username, String password, String email) {
        this.endpoint = endpoint;
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Registry [id=" + id + ", endpoint=" + endpoint  + "]";
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEndpoint() {

        return endpoint;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
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
        Registry other = (Registry) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

}
