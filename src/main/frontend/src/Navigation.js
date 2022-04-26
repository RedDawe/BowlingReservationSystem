import React, {useState} from "react";
import {useNavigate} from "react-router-dom";
import {fetchRole} from "./Utils";

function handleLogout(navigate) {
    fetch('http://localhost:8080/logout', {
        method: 'POST',
    })

    navigate('/');
}

function Navigation() {
    const navigate = useNavigate();

    const [role, setRole] = useState('anonymousUser');
    fetchRole(setRole);

    return (
        <div>
            <button onClick={() => {navigate('/')}}>Home</button>

            {
                (role === 'anonymousUser')
                    ? <div style={{display: 'inline-block'}}>
                        <button onClick={ () => navigate('/user/login') }>Login</button>
                        <button onClick={ () => navigate('/user/register') }>Register</button>
                      </div>
                    : <button onClick={ () => handleLogout(navigate) }>Logout</button>
            }
        </div>
    )
}

export default Navigation;
