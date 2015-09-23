package fr.treeptik.cloudunit.dao;

import fr.treeptik.cloudunit.model.ProxySshPort;
import org.springframework.dao.DataAccessException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProxySshPortDAO extends JpaRepository<ProxySshPort, Integer> {

	@Query("Select p from ProxySshPort p where p.portNumber=:portNumber")
	ProxySshPort findByPortNumber(@Param("portNumber") String portNumber) throws DataAccessException;
	
	@Query("Select min(p.portNumber) from ProxySshPort p where p.used=false")
	String findMinFreePortNumber() throws DataAccessException;
}
