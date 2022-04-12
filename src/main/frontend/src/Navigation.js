import React from "react";
import { useNavigate } from "react-router-dom";

class NavigationComponent extends React.Component {

    render() {
        return (
            <div>
                <button onClick={() => {this.props.navigate('/')}}>Home</button>
            </div>
        )
    }
}

function Navigation(props) {
    const navigate = useNavigate();
    // return <NavigationComponent {...props} navigate={navigate} />
    return (
        <div>
            <button onClick={() => {navigate('/')}}>Home</button>
        </div>
    )
}

export default Navigation;
