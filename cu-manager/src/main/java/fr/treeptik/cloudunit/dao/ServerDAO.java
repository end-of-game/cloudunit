package fr.treeptik.cloudunit.dao;

import fr.treeptik.cloudunit.model.Server;
import org.springframework.dao.DataAccessException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ServerDAO extends JpaRepository<Server, Integer> {

	@Query("Select distinct s " +
			"from Server s " +
			"left join fetch s.portsToOpen p " +
			"left join fetch s.image " +
			"where s.name=:name")
	Server findByName(@Param("name") String name) throws DataAccessException;

	@Query("Select distinct s " +
			"from Server s " +
			"left join fetch s.portsToOpen p" +
			"left join fetch s.image " +
			"where s.application.id=:appId")
	List<Server> findByApp(@Param("appId") Integer applicationId)
			throws DataAccessException;

	@Query("Select distinct s " +
			"from Server s " +
			"left join fetch s.portsToOpen p " +
			"left join fetch s.image " +
			"where s.containerID=:id")
	Server findByContainerID(@Param("id") String id) throws DataAccessException;

}
