package fr.treeptik.springbootredisws.service;

import fr.treeptik.springbootredisws.domain.Weather;
import fr.treeptik.springbootredisws.domain.WeatherEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by guillaume on 18/01/17.
 */
@Service
public class WeatherServiceImpl implements WeatherService {

    private final static DateTimeFormatter DATETIMEFORMATTER = DateTimeFormatter.ofPattern("dd/MM/YYYY HH:mm:ss");

    private final static String KEY = "WEATHER";

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @PostConstruct
    public void init() {
        Weather parisWeather = new Weather(1L, "paris", randomTemperature(), WeatherEnum.RAINY, OffsetDateTime.now());
        redisTemplate.opsForHash().put(KEY, "paris", parisWeather);
    }

    @Override
    public Weather getFromRedis(String name){
        return (Weather) redisTemplate.opsForHash().get(KEY, name);
    }

    private Double randomTemperature(){
        return ThreadLocalRandom.current().nextDouble(-20, 40);
    }



}
