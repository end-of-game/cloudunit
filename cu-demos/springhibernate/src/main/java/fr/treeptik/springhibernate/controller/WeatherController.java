package fr.treeptik.springhibernate.controller;

import fr.treeptik.springhibernate.domain.Weather;
import fr.treeptik.springhibernate.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by guillaume on 18/01/17.
 */
@RestController
@RequestMapping("/weather")
public class WeatherController {

    @Autowired
    private WeatherService weatherService;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public List<Weather> getWeather() {
        return weatherService.findAll();
    }


}
