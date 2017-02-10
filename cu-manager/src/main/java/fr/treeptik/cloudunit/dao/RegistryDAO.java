package fr.treeptik.cloudunit.dao;

import fr.treeptik.cloudunit.model.Registry;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by gborg on 09/02/17.
 */
public interface RegistryDAO extends JpaRepository<Registry, Integer> {
}
