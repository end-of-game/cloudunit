package fr.treeptik.cloudunit.dao;

import fr.treeptik.cloudunit.model.Application;
import fr.treeptik.cloudunit.model.Deployment;
import org.springframework.dao.DataAccessException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DeploymentDAO extends JpaRepository<Deployment, Integer> {

	@Query("select d from Deployment d where d.application = :application")
	List<Deployment> findAllByApplication(
			@Param("application") Application application)
			throws DataAccessException;

}
