package fr.treeptik.cloudunit.dao;

import fr.treeptik.cloudunit.model.ModuleConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ModuleConfigurationDAO extends
		JpaRepository<ModuleConfiguration, Integer> {

}
