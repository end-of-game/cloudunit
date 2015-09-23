package fr.treeptik.cloudunit.dao;

import fr.treeptik.cloudunit.model.Snapshot;
import org.springframework.dao.DataAccessException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SnapshotDAO extends JpaRepository<Snapshot, Integer> {

	@Query("select s from Snapshot s where s.user.login=:login order by s.date DESC")
	public List<Snapshot> listAll(@Param("login") String login)
			throws DataAccessException;

	@Query("select distinct s from Snapshot s join fetch s.images left join fetch s.appConfig where s.user.login=:login and s.tag=:tag")
	public Snapshot findByTagAndUser(@Param("login") String login,
			@Param("tag") String tag) throws DataAccessException;

	@Query("select distinct s from Snapshot s join fetch s.images where s.user.login=:login and s.tag=:tag")
	public Snapshot findAllImagesFromASnapshot(@Param("login") String login,
			@Param("tag") String tag) throws DataAccessException;

}
