package fr.treeptik.cloudunitmonitor.dao;

import fr.treeptik.cloudunit.model.Module;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ModuleDAO
    extends JpaRepository<Module, Integer>
{

}
