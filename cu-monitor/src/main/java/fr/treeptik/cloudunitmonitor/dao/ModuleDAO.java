package fr.treeptik.cloudunitmonitor.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.treeptik.cloudunitmonitor.model.Module;

public interface ModuleDAO
    extends JpaRepository<Module, Integer>
{

}
