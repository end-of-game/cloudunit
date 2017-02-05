import React, {PropTypes} from "react";
import Header from "./common/Header";

class App extends React.Component {
    render() {
        return (
            <div className="container-fluid">
                <Header />
                <div id="main">
                    <div className="wrapper">
                        <section id="content">
                            <div className="container">
                                <div className="section">
                                    {this.props.children}
                                </div>
                            </div>
                        </section>
                    </div>
                </div>
            </div>
        );
    }
}

App.propTypes = {
    children: PropTypes.object.isRequired
};

export default App;