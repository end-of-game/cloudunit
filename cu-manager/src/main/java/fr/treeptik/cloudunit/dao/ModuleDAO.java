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

import fr.treeptik.cloudunit.model.Module;
import org.springframework.dao.DataAccessException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ModuleDAO
    extends JpaRepository<Module, Integer> {

    @Query("Select m from Module m " +
        "left join fetch m.moduleInfos " +
        "left join fetch m.listPorts " +
        "left join fetch m.image " +
        "where m.containerID=:containerID")
    Module findByContainerID(@Param("containerID") String id)
        throws DataAccessException;

    @Query("Select m from Module m " +
        "left join fetch m.moduleInfos " +
        "left join fetch m.listPorts " +
        "left join fetch m.image " +
        "where m.name=:name ")
    Module findByName(@Param("name") String name)
        throws DataAccessException;

    @Query("Select m " +
        "from Module m " +
        "left join fetch m.moduleInfos " +
        "left join fetch m.listPorts " +
        "left join fetch m.image " +
        "where m.application.name=:applicationName " +
        "and m.application.user.id=:userId " +
        "and m.application.cuInstanceName=:cuInstanceName " +
        "order by m.name ASC")
    List<Module> findByAppAndUser(@Param("userId") Integer userId,
                                  @Param("applicationName") String applicationName,
                                  @Param("cuInstanceName") String cuInstanceName)
        throws DataAccessException;

    @Query("Select m from Module m " +
        "left join fetch m.moduleInfos " +
        "left join fetch m.listPorts " +
        "left join fetch m.image " +
        "where m.application.name=:applicationName " +
        "and m.application.cuInstanceName=:cuInstanceName")
    List<Module> findByApp(@Param("applicationName") String applicationName,
                           @Param("cuInstanceName") String cuInstanceName)
        throws DataAccessException;

    @Query("select m from Module m " +
        "left join fetch m.moduleInfos " +
        "left join fetch m.listPorts " +
        "left join fetch m.image " +
        "where m.application.name=:applicationName " +
        "and m.image.name='git' " +
        "and m.application.user.login=:login " +
        "and m.application.cuInstanceName=:cuInstanceName")
    Module findGitModule(@Param("login") String userLogin,
                         @Param("applicationName") String applicationName,
                         @Param("cuInstanceName") String cuInstanceName)
        throws DataAccessException;

}
