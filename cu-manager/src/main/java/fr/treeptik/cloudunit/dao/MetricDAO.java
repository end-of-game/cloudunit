package fr.treeptik.cloudunit.dao;

import fr.treeptik.cloudunit.model.Metric;
import org.springframework.dao.DataAccessException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by guillaume on 04/07/16.
 */
public interface MetricDAO extends JpaRepository<Metric, Integer> {


    @Query("select m from Metric m where m.serverName=:serverName or serverName='all'")
    List<Metric> findAllByServer(@Param("serverName") String serverName) throws DataAccessException;


}
