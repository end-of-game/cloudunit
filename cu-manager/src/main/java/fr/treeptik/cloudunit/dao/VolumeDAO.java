package fr.treeptik.cloudunit.dao;

import fr.treeptik.cloudunit.model.Volume;
import org.springframework.dao.DataAccessException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VolumeDAO extends JpaRepository<Volume, Integer> {

	@Query("Select v from Volume v where v.application.name=:name")
	List<Volume> findByApplicationName(@Param("name") String name) throws DataAccessException;

	@Query("Select v from Volume v where v.containerName=:containerName")
	List<Volume> findByContainer(@Param("containerName") String containerName) throws DataAccessException;

	@Query("Select v from Volume v where v.id=:id")
	Volume findById(@Param("id") int id) throws DataAccessException;

	@Query("select v from Volume v")
	List<Volume> findAllVolumes() throws DataAccessException;
}
