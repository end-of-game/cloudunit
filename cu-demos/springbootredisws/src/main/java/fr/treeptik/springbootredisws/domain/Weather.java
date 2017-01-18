package fr.treeptik.springbootredisws.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.time.OffsetDateTime;

/**
 * Created by guillaume on 18/01/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Weather implements Serializable {

    private final Long id;

    private final String location;

    private final Double temperature;

    private final WeatherEnum weather;

    private final OffsetDateTime dateTime;

    @JsonCreator
    public Weather(@JsonProperty("id") Long id,
                   @JsonProperty("location") String location,
                   @JsonProperty("temperature") Double temperature,
                   @JsonProperty("weather") WeatherEnum weather,
                   @JsonProperty("dateTime") OffsetDateTime dateTime) {
        this.id = id;
        this.location = location;
        this.temperature = temperature;
        this.weather = weather;
        this.dateTime = dateTime;
    }

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

}
