package fr.treeptik.cloudunit.dao;

import fr.treeptik.cloudunit.model.Command;
import org.springframework.dao.DataAccessException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommandDAO extends JpaRepository<Command, Integer> {
    @Query("Select c from Command c where c.id=:id")
    Command findById(@Param("id") int id)
            throws DataAccessException;

    @Query("Select c from Command c where c.image.id=:id")
    List<Command> findByImage(@Param("id") int id)
        throws DataAccessException;
}
