import React from "react";
import { useNavigate } from "react-router-dom";

function Navigation(props) {
    const navigate = useNavigate();
    return (
        <div>
            <button onClick={() => {navigate('/')}}>Home</button>
            <button onClick={() => {navigate('/login')}}>Login</button>
        </div>
    )
}

export default Navigation;
