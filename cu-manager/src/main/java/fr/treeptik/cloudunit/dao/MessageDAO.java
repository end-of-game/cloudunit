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

import fr.treeptik.cloudunit.model.Message;
import fr.treeptik.cloudunit.model.User;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MessageDAO
    extends JpaRepository<Message, Integer> {

    @Query("Select m from Message m where m.author=:user " +
            "and m.cuInstanceName=:cuInstanceName " +
            "order by m.id DESC")
    Page<Message> listByUserAndCuInstance(@Param("user") User user,
                                          @Param("cuInstanceName") String cuInstanceName,
                                          Pageable pageable)
        throws DataAccessException;

    @Query("Select m from Message m where m.applicationName=:applicationName " +
            "and m.author=:user " +
            "and m.cuInstanceName=:cuInstanceName " +
            "order by m.id DESC")
    Page<Message> listByApp(@Param("user") User user,
                            @Param("applicationName") String applicationName,
                            @Param("cuInstanceName") String cuInstanceName,
                            Pageable pageable)
        throws DataAccessException;

    @Query("Select m from Message m  order by m.id DESC")
    Page<Message> listAll(Pageable pageable)
        throws DataAccessException;

    @Modifying
    @Query("delete from Message m where m.author.id=:id")
    void deleteAllUsersMessages(@Param("id") Integer id)
        throws DataAccessException;

}
