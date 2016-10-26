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

package fr.treeptik.cloudunit.dao;

import org.springframework.dao.DataAccessException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import fr.treeptik.cloudunit.model.Server;

public interface ServerDAO extends JpaRepository<Server, Integer> {

	@Query("Select distinct s " + "from Server s " + "left join fetch s.image left join fetch s.application a left join fetch a.user " + "where s.name=:name")
	Server findByName(@Param("name") String name) throws DataAccessException;

	@Query("Select distinct s " + "from Server s " + "left join fetch s.image " + "where s.application.id=:appId")
	Server findByApp(@Param("appId") Integer applicationId) throws DataAccessException;

	@Query("Select distinct s " + "from Server s " + "left join fetch s.image " + "where s.containerID=:id")
	Server findByContainerID(@Param("id") String id) throws DataAccessException;

}
