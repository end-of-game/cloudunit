import * as types from "./actionTypes";
import WeatherAPI from "../api/WeatherAPI";

export function loadLocationsSuccess(users) {
    return {type: types.LOAD_LOCATIONS_SUCCESS, users};
}

export function loadLocations() {
    return function(dispatch) {
        return WeatherAPI.getWeather().then(locations => {
            dispatch(loadLocationsSuccess(locations));
        }).catch(error => {
            throw(error);
        });
    };
}

