package fr.treeptik.springbootredisws.controller;

import fr.treeptik.springbootredisws.domain.Weather;
import fr.treeptik.springbootredisws.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Created by guillaume on 18/01/17.
 */
@RestController
@RequestMapping("/weather")
public class WeatherController {

    @Autowired
    private WeatherService weatherService;

    @RequestMapping(method = RequestMethod.GET, value = "/{name}")
    @ResponseBody
    Weather getWeather(@PathVariable("name") String name) {
        return weatherService.getFromRedis(name);
    }


}
