import React, { useState } from "react";
import { useNavigate } from "react-router-dom";

function getRole() {
    return fetch('http://localhost:8080/api/v1/user/role', {method: 'GET'})
        .then(response => response.json())
}

function MainMenu(props) {
    const navigate = useNavigate();
    const [role, setRole] = useState(() => getRole())

    return (
        <div>
            <button onClick={() => navigate('/reservations/view')}>View reservations</button>
            {
                (role === 'USER') &&
                <div>
                    <button onClick={() => navigate('/reservation/make')}>Make reservation</button>
                    <button onClick={() => navigate('/reservation/delete')}>Delete reservation</button>
                </div>
            }
            {
                (role === 'MANAGER') &&
                <div>
                    <button onClick={() => navigate('/bowling-lane/add')}>Add bowling lane</button>
                    <button onClick={() => navigate('/bowling-lane/remove')}>Remove bowling lane</button>
                </div>
            }
            <br/>
            <button onClick={() => navigate('/user/register')}>Register</button>
        </div>
    )
}

export default MainMenu;
