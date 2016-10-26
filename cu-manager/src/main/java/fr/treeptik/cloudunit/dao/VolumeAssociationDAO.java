package fr.treeptik.cloudunit.dao;

import org.springframework.dao.DataAccessException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import fr.treeptik.cloudunit.model.VolumeAssociation;

public interface VolumeAssociationDAO extends JpaRepository<VolumeAssociation, Integer>{
	
	@Query("select count(va) from VolumeAssociation va join va.volumeAssociationId vaid join vaid.server s where va.path=:path and s.id=:id")
		Integer countVolumeAssociationByPathAndServer(@Param("path") String path, @Param("id") Integer id) throws DataAccessException;
	
}
