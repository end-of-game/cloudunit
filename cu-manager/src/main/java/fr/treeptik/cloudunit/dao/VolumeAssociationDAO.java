package fr.treeptik.cloudunit.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.treeptik.cloudunit.model.VolumeAssociation;

public interface VolumeAssociationDAO extends JpaRepository<VolumeAssociation, Integer>{

}
