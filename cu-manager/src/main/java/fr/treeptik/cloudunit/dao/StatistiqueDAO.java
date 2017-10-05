package fr.treeptik.cloudunit.dao;

import java.util.List;

import fr.treeptik.cloudunit.model.Application;
import fr.treeptik.cloudunit.model.Server;
import fr.treeptik.cloudunit.model.Statistique;
import org.hibernate.exception.DataException;
import org.springframework.dao.DataAccessException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StatistiqueDAO extends JpaRepository<Statistique, Integer> {

    @Query("from Statistique order by id desc")
    public Statistique last() throws DataException;
}
