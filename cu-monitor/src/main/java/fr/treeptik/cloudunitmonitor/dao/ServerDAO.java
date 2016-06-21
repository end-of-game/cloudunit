package fr.treeptik.cloudunitmonitor.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.treeptik.cloudunitmonitor.model.Server;

public interface ServerDAO extends JpaRepository<Server, Integer> {

}
