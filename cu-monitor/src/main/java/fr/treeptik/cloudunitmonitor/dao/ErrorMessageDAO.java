package fr.treeptik.cloudunitmonitor.dao;

import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import fr.treeptik.cloudunit.model.ErrorMessage;

public interface ErrorMessageDAO
    extends JpaRepository<ErrorMessage, Integer>
{

    @Query( "Select e from ErrorMessage e where e.status=1" )
    List<ErrorMessage> findAllUnchecked()
        throws DataAccessException;

}
