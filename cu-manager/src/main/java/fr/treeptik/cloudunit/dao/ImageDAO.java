package fr.treeptik.cloudunit.dao;

import fr.treeptik.cloudunit.model.Image;
import org.springframework.dao.DataAccessException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ImageDAO extends JpaRepository<Image, Integer> {

	@Query("Select i from Image i where i.name=:name")
	Image findByName(@Param("name") String name) throws DataAccessException;

	@Query("select i from Image i where i.status=1")
	List<Image> findAllEnabledImages() throws DataAccessException;

    @Query("select i from Image i where i.status=1 and i.imageType=:imageType")
    List<Image> findAllEnabledImagesByType(@Param("imageType") String imageType) throws DataAccessException;

    @Query("select count(m) from Module m left join m.application a left join m.image i where i.name LIKE %:name and a.name=:appName and a.user.login=:userLogin ")
	Long countNumberOfInstances(@Param("name") String moduleName,
			@Param("appName") String appName ,@Param("userLogin") String userLogin) throws DataAccessException;

}
