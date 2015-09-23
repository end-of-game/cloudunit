package fr.treeptik.cloudunit.dao;

import fr.treeptik.cloudunit.model.Module;
import org.springframework.dao.DataAccessException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ModuleDAO extends JpaRepository<Module, Integer> {

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
			"where m.name=:name")
	Module findByName(@Param("name") String name) throws DataAccessException;

	@Query("Select m " +
			"from Module m " +
			"left join fetch m.moduleInfos " +
			"left join fetch m.listPorts " +
			"left join fetch m.image " +
			"where m.application.name=:applicationName " +
			"and m.application.user.id=:userId " +
			"order by m.name ASC")
	List<Module> findByAppAndUser(@Param("userId") Integer userId,
			@Param("applicationName") String applicationName)
			throws DataAccessException;

	@Query("Select m from Module m " +
			"left join fetch m.moduleInfos " +
			"left join fetch m.listPorts " +
			"left join fetch m.image " +
			"where m.application.name=:applicationName")
	List<Module> findByApp(@Param("applicationName") String applicationName)
			throws DataAccessException;

	@Query("select m from Module m " +
			"left join fetch m.moduleInfos " +
			"left join fetch m.listPorts " +
			"left join fetch m.image " +
			"where m.application.name=:applicationName " +
			"and m.image.name='git' " +
			"and m.application.user.login=:login")
	Module findGitModule(@Param("login") String userLogin,
			@Param("applicationName") String applicationName)
			throws DataAccessException;

}
