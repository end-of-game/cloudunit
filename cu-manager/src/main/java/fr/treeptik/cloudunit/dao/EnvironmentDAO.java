package fr.treeptik.cloudunit.dao;

import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import fr.treeptik.cloudunit.model.EnvironmentVariable;

public interface EnvironmentDAO extends JpaRepository<EnvironmentVariable, Integer> {

	@Query("Select e from EnvironmentVariable e where e.containerName=:containerName")
	List<EnvironmentVariable> findByContainer(@Param("containerName") String containerName) throws DataAccessException;

	@Query("Select e from EnvironmentVariable e where e.application.name=:name")
	List<EnvironmentVariable> findByApplicationName(@Param("name") String name) throws DataAccessException;

	@Query("Select e from EnvironmentVariable e where e.id=:id")
	EnvironmentVariable findById(@Param("id") int id) throws DataAccessException;

	@Query("select e from EnvironmentVariable e")
	List<EnvironmentVariable> findAllEnvironnments() throws DataAccessException;
}
