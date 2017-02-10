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

package fr.treeptik.cloudunit.dto;

import fr.treeptik.cloudunit.model.Registry;

import java.io.Serializable;

/**
 * Created by gborg on 09/02/17.
 */
public class RegistryResource implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;

    private String username;

    private String endpoint;

    private String password;

    private String email;

    public RegistryResource() { }

    public RegistryResource(Registry registry) {
        this.id = registry.getId();
        this.username = registry.getUsername();
        this.endpoint = registry.getEndpoint();
        this.password = registry.getPassword();
        this.email = registry.getEmail();
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public void setId(Integer id) {

        this.id = id;
    }

    public Integer getId() {

        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
}
