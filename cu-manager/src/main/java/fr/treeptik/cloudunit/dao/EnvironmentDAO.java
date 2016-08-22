package fr.treeptik.cloudunit.dao;

import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import fr.treeptik.cloudunit.model.Environment;

public interface EnvironmentDAO extends JpaRepository<Environment, Integer> {

	@Query("Select e from Environment e where e.containerName=:containerName")
	List<Environment> findByContainer(@Param("containerName") String containerName) throws DataAccessException;

	@Query("Select e from Environment e where e.application.name=:name")
	List<Environment> findByApplicationName(@Param("name") String name) throws DataAccessException;

	@Query("Select e from Environment e where e.id=:id")
	Environment findById(@Param("id") int id) throws DataAccessException;

	@Query("select e from Environment e")
	List<Environment> findAllEnvironnments() throws DataAccessException;
}
