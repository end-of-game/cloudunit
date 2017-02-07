package fr.treeptik.springhibernate.dao;

import fr.treeptik.springhibernate.domain.Weather;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by guillaume on 07/02/17.
 */
public interface WeatherDAO extends JpaRepository<Weather, Long> {

}
