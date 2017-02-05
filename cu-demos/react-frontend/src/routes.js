import React from "react";
import {Route, IndexRoute} from "react-router";
import App from "./components/App";
import WeatherPage from "./components/weather/WeatherPage"

export default (
    <Route path="/" component={App}>
        <IndexRoute component={WeatherPage} />
    </Route>
);