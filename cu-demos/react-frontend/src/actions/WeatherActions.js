import * as types from "./actionTypes";
import LocationApi from "../api/LocationAPI";

export function loadLocationsSuccess(locations) {
    return {type: types.LOAD_LOCATIONS_SUCCESS, locations};
}

export function loadLocations() {

    return function(dispatch) {
        return LocationApi.getAllLocations().then(locations => {
            dispatch(loadLocationsSuccess(locations));
        }).catch(error => {
            throw(error);
        });
    };
}