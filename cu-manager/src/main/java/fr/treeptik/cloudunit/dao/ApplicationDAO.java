package fr.treeptik.cloudunit.dao;

import fr.treeptik.cloudunit.model.Application;
import org.springframework.dao.DataAccessException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ApplicationDAO extends JpaRepository<Application, Integer> {

	@Query("Select distinct a from Application a " +
			"join fetch a.servers s " +
			"join fetch a.modules m " +
			"left join fetch a.deployments " +
			"left join fetch a.aliases " +
			"left join fetch m.moduleInfos " +
			"left join fetch m.listPorts " +
			"left join fetch s.listPorts " +
			"left join fetch s.portsToOpen " +
			"where a.user.id=:userId and a.name=:name")
	Application findByNameAndUser(@Param("userId") Integer userId,
			@Param("name") String name) throws DataAccessException;

	@Query("Select distinct a from Application a " +
			"join fetch a.servers " +
			"join fetch a.modules " +
			"left join fetch a.deployments " +
			"left join fetch a.aliases " +
			"where a.user.id=:userId")
	public List<Application> findAllByUser(@Param("userId") Integer userId)
			throws DataAccessException;

	@Query("select al from Application a left join a.aliases al where a.name=:name")
	public List<String> findAllAliases(@Param("name") String applicationName)
			throws DataAccessException;

	@Query("select al from Application a left join a.aliases al")
	public List<String> findAliasesForAllApps() throws DataAccessException;

	@Query("Select a from Application a where a.name=:name and a.user.login=:login")
	Application findByUserLoginAndName(@Param("login") String userLogin,
			@Param("name") String applicationName) throws DataAccessException;

	@Query("Select count(a) from Application a where a.user.id=:userId")
	Long countApp(@Param("userId") Integer userId) throws DataAccessException;

}
