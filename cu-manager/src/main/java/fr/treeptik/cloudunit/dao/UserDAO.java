package fr.treeptik.cloudunit.dao;

import fr.treeptik.cloudunit.model.User;
import org.springframework.dao.DataAccessException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserDAO extends JpaRepository<User, Integer> {

	@Query("Select u from User u where u.email=:email")
	List<User> findByEmail(@Param("email") String email)
			throws DataAccessException;

	@Query("Select u from User u where u.login=:login")
	User findByLogin(@Param("login") String login) throws DataAccessException;

}
