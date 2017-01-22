import React from 'react';
import * as ReactDOM from "react-dom";

class Title extends React.Component {
    render() {
        return <h1>Bienvenue sur la page météo</h1>
    }

}
ReactDOM.render(<Title/>, document.getElementById('title'));