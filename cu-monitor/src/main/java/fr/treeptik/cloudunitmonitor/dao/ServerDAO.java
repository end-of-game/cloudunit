package fr.treeptik.cloudunitmonitor.dao;

import fr.treeptik.cloudunit.model.Server;
import org.springframework.dao.DataAccessException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import javax.xml.crypto.Data;
import java.util.List;


public interface ServerDAO extends JpaRepository<Server, Integer> {

    @Query("select s from Server s left join fetch s.application a left join fetch a.portsToOpen")
    Server findOne() throws DataAccessException;

}
