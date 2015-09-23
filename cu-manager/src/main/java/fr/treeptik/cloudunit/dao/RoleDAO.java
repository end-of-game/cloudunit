package fr.treeptik.cloudunit.dao;

import fr.treeptik.cloudunit.model.Role;
import org.springframework.dao.DataAccessException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RoleDAO extends JpaRepository<Role, Integer> {

	@Query("select r from Role r where r.description=:desc")
	Role findByRole(@Param("desc") String description) throws DataAccessException;

}
