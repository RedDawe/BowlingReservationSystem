import React from "react";
import { useNavigate } from "react-router-dom";

function handleLogout(navigate) {
    fetch('http://localhost:8080/api/v1/user/logout', {
        method: 'POST',
    })

    navigate('/');
}

function Navigation(props) {
    const navigate = useNavigate();
    return (
        <div>
            <button onClick={() => {navigate('/')}}>Home</button>
            <button onClick={() => {navigate('/login')}}>Login</button>
            <button onClick={() => {handleLogout(navigate)}}>Logout</button>
        </div>
    )
}

export default Navigation;
