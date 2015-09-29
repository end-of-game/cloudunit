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

package fr.treeptik.cloudunit.dao;

import fr.treeptik.cloudunit.model.Snapshot;
import org.springframework.dao.DataAccessException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SnapshotDAO
    extends JpaRepository<Snapshot, Integer> {

    @Query("select s from Snapshot s where s.user.login=:login order by s.date DESC")
    public List<Snapshot> listAll(@Param("login") String login)
        throws DataAccessException;

    @Query("select distinct s from Snapshot s join fetch s.images left join fetch s.appConfig where s.user.login=:login and s.tag=:tag")
    public Snapshot findByTagAndUser(@Param("login") String login,
                                     @Param("tag") String tag)
        throws DataAccessException;

    @Query("select distinct s from Snapshot s join fetch s.images where s.user.login=:login and s.tag=:tag")
    public Snapshot findAllImagesFromASnapshot(@Param("login") String login,
                                               @Param("tag") String tag)
        throws DataAccessException;

}
