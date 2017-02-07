package fr.treeptik.springhibernate.service;

import fr.treeptik.springhibernate.domain.Weather;

import java.util.List;

/**
 * Created by guillaume on 18/01/17.
 */
public interface WeatherService {

    List<Weather> findAll();
}
