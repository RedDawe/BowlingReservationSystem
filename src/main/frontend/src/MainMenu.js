import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { fetchRole } from "./Utils";

function MainMenu() {
    const navigate = useNavigate();

    const [role, setRole] = useState('anonymousUser');
    fetchRole(setRole);

    return (
        <div>
            <button onClick={() => navigate('/reservations/view')}>View reservations</button>
            {
                (role === 'USER') &&
                <div style={{display: 'inline-block'}}>
                    <button onClick={() => navigate('/reservation/make')}>Make reservation</button>
                    <button onClick={() => navigate('/reservation/delete')}>Delete reservation</button>
                </div>
            }
            {
                (role === 'MANAGER') &&
                <div style={{display: 'inline-block'}}>
                    <button onClick={() => navigate('/bowling-lane/add')}>Add bowling lane</button>
                    <button onClick={() => navigate('/bowling-lane/remove')}>Remove bowling lane</button>
                </div>
            }
        </div>
    )
}

export default MainMenu;
