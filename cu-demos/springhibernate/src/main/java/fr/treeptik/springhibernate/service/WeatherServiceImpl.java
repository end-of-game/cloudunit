package fr.treeptik.springhibernate.service;

import fr.treeptik.springhibernate.dao.WeatherDAO;
import fr.treeptik.springhibernate.domain.Weather;
import fr.treeptik.springhibernate.domain.WeatherEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Created by guillaume on 18/01/17.
 */
@Service
public class WeatherServiceImpl implements WeatherService {

    @Autowired
    private WeatherDAO weatherDAO;

    @PostConstruct
    @Transactional
    public void init() {
        IntStream.range(0, 5).forEach(t -> {
            Weather weather = new Weather();
            weather.setDateTime(LocalDateTime.now());
            weather.setTemperature(32d);
            weather.setLocation("Paris");
            weather.setWeather(WeatherEnum.CLOUDY);
            weatherDAO.save(weather);
        });


    }

    @Override
    public List<Weather> findAll() {
        return weatherDAO.findAll();
    }


}
