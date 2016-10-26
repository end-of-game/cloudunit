package fr.treeptik.cloudunit.dao;

import fr.treeptik.cloudunit.model.Port;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by guillaume on 25/09/16.
 */
public interface PortDAO extends JpaRepository<Port, Integer>{
}
