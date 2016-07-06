package fr.treeptik.cloudunitmonitor.dao;

import fr.treeptik.cloudunit.model.Server;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ServerDAO extends JpaRepository<Server, Integer> {

}
