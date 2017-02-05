import React, {PropTypes} from "react";
import {Link, browserHistory} from "react-router";
import {connect} from "react-redux";
import {bindActionCreators} from "redux";
import LocationList from "./LocationList";
import * as actions from "../../actions/WeatherActions";

class WeatherPage extends React.Component {
    render() {
        const locations = this.props.locations;
        return (
            <div className="col-md-12">
                <h1>Hot spot météo</h1>
                <div className="col-md-4">
                    <LocationList locations={locations}/>
                </div>
                <div className="col-md-8">
                    {this.props.children}
                </div>
            </div>
        );
    }
}

WeatherPage.propTypes = {
    locations: PropTypes.array.isRequired,
    children: PropTypes.object
};

function mapStateToProps(state, ownProps) {
    if (state.locations.length > 0) {
        return {
            locations: state.locations
        };
    } else {
        return {
            locations: [{id: ''}]
        }
    }
}

function mapDispatchToProps(dispatch) {
    return {actions: bindActionCreators(actions, dispatch)}
}

export default connect(mapStateToProps, mapDispatchToProps)(WeatherPage);