import React from "react";
import { useNavigate } from "react-router-dom";

function MainMenu(props) {
    const navigate = useNavigate();
    return (
        <div>
            <button onClick={() => navigate('/reservation/make')}>Make reservation</button>
            <button onClick={() => navigate('/reservations/view')}>View reservations</button>
            <button onClick={() => navigate('/reservation/delete')}>Delete reservation</button>
            <button onClick={() => navigate('/bowling-lane/add')}>Add bowling lane</button>
            <button onClick={() => navigate('/bowling-lane/remove')}>Remove bowling lane</button>
            <br/>
            <button onClick={() => navigate('/user/register')}>Register</button>
        </div>
    )
}

export default MainMenu;
