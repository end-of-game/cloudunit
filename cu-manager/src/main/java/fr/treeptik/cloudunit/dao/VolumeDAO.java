package fr.treeptik.cloudunit.dao;

import fr.treeptik.cloudunit.model.Volume;
import org.springframework.dao.DataAccessException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VolumeDAO extends JpaRepository<Volume, Integer> {

	@Query("Select v from Volume v where v.id=:id")
	Volume findById(@Param("id") int id) throws DataAccessException;

	@Query("Select v from Volume v where v.name=:name")
	Volume findByName(@Param("name") String name) throws DataAccessException;

	@Query("select v from Volume v")
	List<Volume> findAllVolumes() throws DataAccessException;

	@Query("select v from Volume v join fetch v.volumeAssociations va join va.volumeAssociationId vaid join vaid.server s where s.name=:containerName")
	List<Volume> findVolumesByContainerName(@Param("containerName") String containerName) throws DataAccessException;

}
