package fr.treeptik.cloudunit.dao;

import fr.treeptik.cloudunit.model.Registry;
import org.springframework.dao.DataAccessException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RegistryDAO extends JpaRepository<Registry, Integer> {
    @Query("Select v from Registry v where v.id=:id")
    Registry findById(@Param("id") int id) throws DataAccessException;
}
