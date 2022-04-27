import React from "react";
import { useNavigate } from "react-router-dom";

class RegistrationComponent extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            username: '',
            password: ''
        }

        this.handleChange = this.handleChange.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
    }

    handleSubmit(event) {
        fetch('/api/v1/user/register', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(this.state)

        })

        this.props.navigate('/user/login');
    }

    handleChange(event) {
        this.setState({[event.target.name]: event.target.value})
    }

    render() {
        return(
            <div>
                <form onSubmit={this.handleSubmit}>
                    <input type={"text"} value={this.state.username} onChange={this.handleChange} name={"username"} />
                    <input type={"password"} value={this.state.password} onChange={this.handleChange} name={"password"} />

                    <input type={"submit"} value={"Register"} />
                </form>
            </div>
        )
    }
}

function Registration(props) {
    const navigate = useNavigate();
    return <RegistrationComponent {...props} navigate={navigate} />
}

export default Registration;
