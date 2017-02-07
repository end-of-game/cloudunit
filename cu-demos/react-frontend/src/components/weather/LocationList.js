import React, {PropTypes} from "react";
import {Link} from "react-router";


const LocationList = ({locations}) => {
    return (
            <div className="row">
                    {locations.map(l =>
                    <div className="col l4" key={l.id}>
                        <div className="card  cyan">
                            <div className="card-content white-text">
                                <span className="card-title">{l.location}</span>
                                <p>La météo sur {l.location} est {l.weather}</p>
                            </div>
                            <div className="card-action">
                                <a href="#" className="lime-text text-accent-1">Il fait actuellement {l.temperature} °C</a>
                            </div>
                        </div>
                    </div>
                    )}
        </div>
    );
};

LocationList.propTypes = {
    locations: PropTypes.array.isRequired
};

export default LocationList;