package fr.treeptik.springhibernate.domain;

import javax.persistence.*;
import java.io.Serializable;
import java.time.OffsetDateTime;

/**
 * Created by guillaume on 18/01/17.
 */
@Entity
public class Weather implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String location;

    private Double temperature;

    @Enumerated(EnumType.STRING)
    private WeatherEnum weather;

    private OffsetDateTime dateTime;

    public Long getId() {
        return id;
    }

    public String getLocation() {
        return location;
    }

    public Double getTemperature() {
        return temperature;
    }


    public WeatherEnum getWeather() {
        return weather;
    }


    public OffsetDateTime getDateTime() {
        return dateTime;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public void setWeather(WeatherEnum weather) {
        this.weather = weather;
    }

    public void setDateTime(OffsetDateTime dateTime) {
        this.dateTime = dateTime;
    }
}
