import React, {PropTypes} from "react";
import {Link, IndexLink} from "react-router";
import {connect} from "react-redux";
import {bindActionCreators} from "redux";

class Header extends React.Component {
    constructor(props) {
        super();
    }

    render() {
        return (
            <div className="col s12 m8 l9">
                <nav className="cyan">
                    <div className="nav-wrapper">
                        <div className="col s12">
                            <a href="#!" className="brand-logo">&nbsp;&nbsp;&nbsp;Bienvenue sur la page météo</a>
                        </div>
                    </div>
                </nav>
            </div>

        );
    }
}

Header.propTypes = {}

export default Header;
